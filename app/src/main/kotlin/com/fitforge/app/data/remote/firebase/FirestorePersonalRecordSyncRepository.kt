package com.fitforge.app.data.remote.firebase

import com.fitforge.app.data.local.db.entity.PersonalRecordEntity
import com.fitforge.app.domain.repository.CloudPersonalRecordSyncRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class FirestorePersonalRecordSyncRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CloudPersonalRecordSyncRepository {

    override suspend fun pushPersonalRecord(record: PersonalRecordEntity): Result<Unit> {
        return runCatching {
            val payload = record.toMap()
            personalRecordsCollection()
                .document(record.id)
                .set(payload)
                .await()
            Unit
        }
    }

    override suspend fun pullPersonalRecordsForUser(userId: String): Result<List<PersonalRecordEntity>> {
        return runCatching {
            val snapshot = personalRecordsCollection()
                .whereEqualTo("userId", userId)
                .orderBy("achievedAtEpochMillis")
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toPersonalRecordEntity()
            }
        }
    }

    private fun personalRecordsCollection() = firestore.collection(PERSONAL_RECORDS_COLLECTION)

    private companion object {
        const val PERSONAL_RECORDS_COLLECTION = "personal_records"
    }
}

private fun PersonalRecordEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "userId" to userId,
    "exerciseId" to exerciseId,
    "workoutId" to workoutId,
    "metricType" to metricType,
    "value" to value,
    "unit" to unit,
    "achievedAtEpochMillis" to achievedAtEpochMillis,
    "notes" to notes,
)

private fun com.google.firebase.firestore.DocumentSnapshot.toPersonalRecordEntity(): PersonalRecordEntity? {
    val id = getString("id") ?: return null
    val userId = getString("userId") ?: return null
    val metricType = getString("metricType") ?: return null
    val value = getDouble("value") ?: return null
    val unit = getString("unit") ?: return null
    val achievedAtEpochMillis = getLong("achievedAtEpochMillis") ?: return null

    return PersonalRecordEntity(
        id = id,
        userId = userId,
        exerciseId = getString("exerciseId"),
        workoutId = getString("workoutId"),
        metricType = metricType,
        value = value,
        unit = unit,
        achievedAtEpochMillis = achievedAtEpochMillis,
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
