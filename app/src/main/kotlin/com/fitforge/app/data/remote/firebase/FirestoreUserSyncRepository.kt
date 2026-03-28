package com.fitforge.app.data.remote.firebase

import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.domain.repository.CloudUserSyncRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class FirestoreUserSyncRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CloudUserSyncRepository {

    override suspend fun pushUser(user: UserEntity): Result<Unit> {
        return runCatching {
            val payload = user.toMap()
            usersCollection()
                .document(user.id)
                .set(payload)
                .await()
            Unit
        }
    }

    override suspend fun pullUser(userId: String): Result<UserEntity?> {
        return runCatching {
            val snapshot = usersCollection()
                .document(userId)
                .get()
                .await()

            snapshot.toUserEntity()
        }
    }

    private fun usersCollection() = firestore.collection(USERS_COLLECTION)

    private companion object {
        const val USERS_COLLECTION = "users"
    }
}

private fun UserEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "displayName" to displayName,
    "email" to email,
    "photoUrl" to photoUrl,
    "birthDateEpochMillis" to birthDateEpochMillis,
    "gender" to gender,
    "heightCm" to heightCm,
    "currentWeightKg" to currentWeightKg,
    "targetWeightKg" to targetWeightKg,
    "fitnessLevel" to fitnessLevel,
    "primaryGoals" to primaryGoals.toList(),
    "workoutLocations" to workoutLocations.toList(),
    "availableEquipment" to availableEquipment.toList(),
    "preferredUnitSystem" to preferredUnitSystem,
    "isNotificationEnabled" to isNotificationEnabled,
    "isHealthConnectLinked" to isHealthConnectLinked,
    "isPremium" to isPremium,
    "createdAtEpochMillis" to createdAtEpochMillis,
    "updatedAtEpochMillis" to updatedAtEpochMillis,
)

private fun com.google.firebase.firestore.DocumentSnapshot.toUserEntity(): UserEntity? {
    val id = getString("id") ?: return null
    val displayName = getString("displayName") ?: return null
    val gender = getString("gender") ?: "unspecified"
    val fitnessLevel = getString("fitnessLevel") ?: "beginner"
    val preferredUnitSystem = getString("preferredUnitSystem") ?: "metric"

    @Suppress("UNCHECKED_CAST")
    val primaryGoals = (get("primaryGoals") as? List<String>)?.toSet() ?: emptySet()
    @Suppress("UNCHECKED_CAST")
    val workoutLocations = (get("workoutLocations") as? List<String>)?.toSet() ?: emptySet()
    @Suppress("UNCHECKED_CAST")
    val availableEquipment = (get("availableEquipment") as? List<String>)?.toSet() ?: emptySet()

    return UserEntity(
        id = id,
        displayName = displayName,
        email = getString("email"),
        photoUrl = getString("photoUrl"),
        birthDateEpochMillis = getLong("birthDateEpochMillis"),
        gender = gender,
        heightCm = getDouble("heightCm")?.toFloat(),
        currentWeightKg = getDouble("currentWeightKg")?.toFloat(),
        targetWeightKg = getDouble("targetWeightKg")?.toFloat(),
        fitnessLevel = fitnessLevel,
        primaryGoals = primaryGoals,
        workoutLocations = workoutLocations,
        availableEquipment = availableEquipment,
        preferredUnitSystem = preferredUnitSystem,
        isNotificationEnabled = getBoolean("isNotificationEnabled") ?: true,
        isHealthConnectLinked = getBoolean("isHealthConnectLinked") ?: false,
        isPremium = getBoolean("isPremium") ?: false,
        createdAtEpochMillis = getLong("createdAtEpochMillis") ?: System.currentTimeMillis(),
        updatedAtEpochMillis = getLong("updatedAtEpochMillis") ?: System.currentTimeMillis(),
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
