package com.fitforge.app.data.remote.firebase

import com.fitforge.app.data.local.db.entity.ExerciseSetEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity
import com.fitforge.app.domain.repository.CloudWorkoutSyncRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class FirestoreWorkoutSyncRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CloudWorkoutSyncRepository {

    override suspend fun pushWorkout(workout: WorkoutEntity): Result<Unit> {
        return runCatching {
            val payload = workout.toMap()
            workoutsCollection()
                .document(workout.id)
                .set(payload)
                .await()
            Unit
        }
    }

    override suspend fun pushWorkoutExercises(workoutExercises: List<WorkoutExerciseEntity>): Result<Unit> {
        return runCatching {
            if (workoutExercises.isEmpty()) return@runCatching
            val batch = firestore.batch()
            workoutExercises.forEach { workoutExercise ->
                val docRef = workoutExercisesCollection()
                    .document(workoutExercise.id)
                batch.set(docRef, workoutExercise.toMap())
            }
            batch.commit().await()
            Unit
        }
    }

    override suspend fun pushExerciseSets(exerciseSets: List<ExerciseSetEntity>): Result<Unit> {
        return runCatching {
            if (exerciseSets.isEmpty()) return@runCatching
            val batch = firestore.batch()
            exerciseSets.forEach { exerciseSet ->
                val docRef = exerciseSetsCollection()
                    .document(exerciseSet.id)
                batch.set(docRef, exerciseSet.toMap())
            }
            batch.commit().await()
            Unit
        }
    }

    override suspend fun pullWorkoutsForUser(userId: String): Result<List<WorkoutEntity>> {
        return runCatching {
            val snapshot = workoutsCollection()
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toWorkoutEntity()
            }
        }
    }

    override suspend fun pullWorkoutExercisesForWorkout(workoutId: String): Result<List<WorkoutExerciseEntity>> {
        return runCatching {
            val snapshot = workoutExercisesCollection()
                .whereEqualTo("workoutId", workoutId)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toWorkoutExerciseEntity()
            }
        }
    }

    override suspend fun pullExerciseSetsForWorkout(workoutId: String): Result<List<ExerciseSetEntity>> {
        return runCatching {
            val snapshot = exerciseSetsCollection()
                .whereEqualTo("workoutId", workoutId)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toExerciseSetEntity()
            }
        }
    }

    private fun workoutsCollection() = firestore.collection(WORKOUTS_COLLECTION)
    private fun workoutExercisesCollection() = firestore.collection(WORKOUT_EXERCISES_COLLECTION)
    private fun exerciseSetsCollection() = firestore.collection(EXERCISE_SETS_COLLECTION)

    private companion object {
        const val WORKOUTS_COLLECTION = "workouts"
        const val WORKOUT_EXERCISES_COLLECTION = "workout_exercises"
        const val EXERCISE_SETS_COLLECTION = "exercise_sets"
    }
}

private fun WorkoutEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "userId" to userId,
    "name" to name,
    "description" to description,
    "workoutType" to workoutType,
    "difficulty" to difficulty,
    "estimatedDurationMinutes" to estimatedDurationMinutes,
    "estimatedCalories" to estimatedCalories,
    "source" to source,
    "scheduledDateEpochMillis" to scheduledDateEpochMillis,
    "completedAtEpochMillis" to completedAtEpochMillis,
    "status" to status,
    "notes" to notes,
    "createdAtEpochMillis" to createdAtEpochMillis,
    "updatedAtEpochMillis" to updatedAtEpochMillis,
)

private fun com.google.firebase.firestore.DocumentSnapshot.toWorkoutEntity(): WorkoutEntity? {
    val id = getString("id") ?: return null
    val name = getString("name") ?: return null
    val description = getString("description") ?: ""
    val workoutType = getString("workoutType") ?: "unknown"
    val difficulty = getString("difficulty") ?: "beginner"
    val estimatedDurationMinutes = getLong("estimatedDurationMinutes")?.toInt() ?: 0
    val estimatedCalories = getLong("estimatedCalories")?.toInt()
    val source = getString("source") ?: "cloud"
    val scheduledDateEpochMillis = getLong("scheduledDateEpochMillis")
    val completedAtEpochMillis = getLong("completedAtEpochMillis")
    val status = getString("status") ?: "planned"
    val notes = getString("notes")
    val createdAtEpochMillis = getLong("createdAtEpochMillis") ?: System.currentTimeMillis()
    val updatedAtEpochMillis = getLong("updatedAtEpochMillis") ?: createdAtEpochMillis

    return WorkoutEntity(
        id = id,
        userId = getString("userId"),
        name = name,
        description = description,
        workoutType = workoutType,
        difficulty = difficulty,
        estimatedDurationMinutes = estimatedDurationMinutes,
        estimatedCalories = estimatedCalories,
        source = source,
        scheduledDateEpochMillis = scheduledDateEpochMillis,
        completedAtEpochMillis = completedAtEpochMillis,
        status = status,
        notes = notes,
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
    )
}

private fun WorkoutExerciseEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "workoutId" to workoutId,
    "exerciseId" to exerciseId,
    "sequenceIndex" to sequenceIndex,
    "targetSets" to targetSets,
    "targetRepsMin" to targetRepsMin,
    "targetRepsMax" to targetRepsMax,
    "targetDurationSeconds" to targetDurationSeconds,
    "targetDistanceMeters" to targetDistanceMeters,
    "targetWeightKg" to targetWeightKg,
    "restDurationSeconds" to restDurationSeconds,
    "supersetGroup" to supersetGroup,
    "notes" to notes,
    "isOptional" to isOptional,
)

private fun com.google.firebase.firestore.DocumentSnapshot.toWorkoutExerciseEntity(): WorkoutExerciseEntity? {
    val id = getString("id") ?: return null
    val workoutId = getString("workoutId") ?: return null
    val exerciseId = getString("exerciseId") ?: return null
    val sequenceIndex = getLong("sequenceIndex")?.toInt() ?: return null
    val targetSets = getLong("targetSets")?.toInt() ?: return null

    return WorkoutExerciseEntity(
        id = id,
        workoutId = workoutId,
        exerciseId = exerciseId,
        sequenceIndex = sequenceIndex,
        targetSets = targetSets,
        targetRepsMin = getLong("targetRepsMin")?.toInt(),
        targetRepsMax = getLong("targetRepsMax")?.toInt(),
        targetDurationSeconds = getLong("targetDurationSeconds")?.toInt(),
        targetDistanceMeters = getDouble("targetDistanceMeters")?.toFloat(),
        targetWeightKg = getDouble("targetWeightKg")?.toFloat(),
        restDurationSeconds = getLong("restDurationSeconds")?.toInt(),
        supersetGroup = getString("supersetGroup"),
        notes = getString("notes"),
        isOptional = getBoolean("isOptional") ?: false,
    )
}

private fun ExerciseSetEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "workoutId" to workoutId,
    "workoutExerciseId" to workoutExerciseId,
    "exerciseId" to exerciseId,
    "setNumber" to setNumber,
    "reps" to reps,
    "weightKg" to weightKg,
    "durationSeconds" to durationSeconds,
    "distanceMeters" to distanceMeters,
    "restDurationSeconds" to restDurationSeconds,
    "rpe" to rpe,
    "isWarmUp" to isWarmUp,
    "completedAtEpochMillis" to completedAtEpochMillis,
    "notes" to notes,
)

private fun com.google.firebase.firestore.DocumentSnapshot.toExerciseSetEntity(): ExerciseSetEntity? {
    val id = getString("id") ?: return null
    val workoutId = getString("workoutId") ?: return null
    val workoutExerciseId = getString("workoutExerciseId") ?: return null
    val exerciseId = getString("exerciseId") ?: return null
    val setNumber = getLong("setNumber")?.toInt() ?: return null

    return ExerciseSetEntity(
        id = id,
        workoutId = workoutId,
        workoutExerciseId = workoutExerciseId,
        exerciseId = exerciseId,
        setNumber = setNumber,
        reps = getLong("reps")?.toInt(),
        weightKg = getDouble("weightKg")?.toFloat(),
        durationSeconds = getLong("durationSeconds")?.toInt(),
        distanceMeters = getDouble("distanceMeters")?.toFloat(),
        restDurationSeconds = getLong("restDurationSeconds")?.toInt(),
        rpe = getDouble("rpe")?.toFloat(),
        isWarmUp = getBoolean("isWarmUp") ?: false,
        completedAtEpochMillis = getLong("completedAtEpochMillis"),
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
