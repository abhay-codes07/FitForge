package com.fitforge.app.data.remote.firebase

import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import com.fitforge.app.domain.repository.CloudBodyMeasurementSyncRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class FirestoreBodyMeasurementSyncRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CloudBodyMeasurementSyncRepository {

    override suspend fun pushBodyMeasurement(measurement: BodyMeasurementEntity): Result<Unit> {
        return runCatching {
            val payload = measurement.toMap()
            bodyMeasurementsCollection()
                .document(measurement.id)
                .set(payload)
                .await()
            Unit
        }
    }

    override suspend fun pullBodyMeasurementsForUser(userId: String): Result<List<BodyMeasurementEntity>> {
        return runCatching {
            val snapshot = bodyMeasurementsCollection()
                .whereEqualTo("userId", userId)
                .orderBy("recordedAtEpochMillis")
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toBodyMeasurementEntity()
            }
        }
    }

    private fun bodyMeasurementsCollection() = firestore.collection(BODY_MEASUREMENTS_COLLECTION)

    private companion object {
        const val BODY_MEASUREMENTS_COLLECTION = "body_measurements"
    }
}

private fun BodyMeasurementEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "userId" to userId,
    "recordedAtEpochMillis" to recordedAtEpochMillis,
    "weightKg" to weightKg,
    "bodyFatPercent" to bodyFatPercent,
    "chestCm" to chestCm,
    "waistCm" to waistCm,
    "hipsCm" to hipsCm,
    "bicepsCm" to bicepsCm,
    "thighCm" to thighCm,
    "calfCm" to calfCm,
    "neckCm" to neckCm,
    "progressPhotoUris" to progressPhotoUris,
    "notes" to notes,
)

private fun com.google.firebase.firestore.DocumentSnapshot.toBodyMeasurementEntity(): BodyMeasurementEntity? {
    val id = getString("id") ?: return null
    val userId = getString("userId") ?: return null
    val recordedAtEpochMillis = getLong("recordedAtEpochMillis") ?: return null

    @Suppress("UNCHECKED_CAST")
    val progressPhotoUris = (get("progressPhotoUris") as? List<String>) ?: emptyList()

    return BodyMeasurementEntity(
        id = id,
        userId = userId,
        recordedAtEpochMillis = recordedAtEpochMillis,
        weightKg = getDouble("weightKg")?.toFloat(),
        bodyFatPercent = getDouble("bodyFatPercent")?.toFloat(),
        chestCm = getDouble("chestCm")?.toFloat(),
        waistCm = getDouble("waistCm")?.toFloat(),
        hipsCm = getDouble("hipsCm")?.toFloat(),
        bicepsCm = getDouble("bicepsCm")?.toFloat(),
        thighCm = getDouble("thighCm")?.toFloat(),
        calfCm = getDouble("calfCm")?.toFloat(),
        neckCm = getDouble("neckCm")?.toFloat(),
        progressPhotoUris = progressPhotoUris,
        notes = getString("notes"),
    )
}

private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result)
        } else {
            continuation.resumeWithException(task.exception ?: IllegalStateException("Task failed."))
        }
    }
}
