package com.fitforge.app.domain.usecase.active_workout

import com.fitforge.app.data.local.db.entity.ExerciseEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity
import com.fitforge.app.domain.repository.ExerciseRepository
import com.fitforge.app.domain.repository.ExerciseSetRepository
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutExerciseRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

private const val FOURTEEN_DAYS_MILLIS = 14L * 24L * 60L * 60L * 1000L

data class ActiveWorkoutExerciseStep(
    val workoutExerciseId: String,
    val exerciseId: String,
    val exerciseName: String,
    val imageUrl: String?,
    val targetSets: Int,
    val targetReps: Int,
    val restSeconds: Int,
)

data class ActiveWorkoutSessionData(
    val workoutId: String,
    val workoutTitle: String,
    val exercises: List<ActiveWorkoutExerciseStep>,
    val currentExerciseIndex: Int,
    val completedSetCounts: Map<String, Int>,
)

class GetActiveWorkoutSessionUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val workoutExerciseRepository: WorkoutExerciseRepository,
    private val exerciseRepository: ExerciseRepository,
    private val exerciseSetRepository: ExerciseSetRepository,
) {
    suspend operator fun invoke(nowEpochMillis: Long = System.currentTimeMillis()): ActiveWorkoutSessionData {
        val user = userRepository.getPrimaryUser() ?: error("No primary user found")

        val scheduledWorkouts = workoutRepository.observeScheduledWorkoutsForRange(
            userId = user.id,
            startEpochMillis = nowEpochMillis,
            endEpochMillis = nowEpochMillis + FOURTEEN_DAYS_MILLIS,
        ).first()

        val workout = scheduledWorkouts.firstOrNull { it.status != "completed" }
            ?: workoutRepository.observeWorkoutsForUser(user.id).first().firstOrNull { it.status != "completed" }
            ?: error("No workout is available to start")

        val workoutExercises = workoutExerciseRepository.getExercisesForWorkout(workout.id)
            .sortedBy { it.sequenceIndex }

        if (workoutExercises.isEmpty()) {
            error("Selected workout does not have any exercises")
        }

        val steps = buildSteps(workoutExercises)
        if (steps.isEmpty()) {
            error("Selected workout has invalid exercise references")
        }

        val loggedSets = exerciseSetRepository.observeSetsForWorkout(workout.id).first()
        val completedSetCounts = loggedSets
            .filter { it.completedAtEpochMillis != null }
            .groupingBy { it.workoutExerciseId }
            .eachCount()

        val currentExerciseIndex = steps.indexOfFirst { step ->
            val completedForStep = completedSetCounts[step.workoutExerciseId] ?: 0
            completedForStep < step.targetSets
        }.let { if (it == -1) steps.lastIndex else it }

        return ActiveWorkoutSessionData(
            workoutId = workout.id,
            workoutTitle = workout.name,
            exercises = steps,
            currentExerciseIndex = currentExerciseIndex,
            completedSetCounts = completedSetCounts,
        )
    }

    private suspend fun buildSteps(workoutExercises: List<WorkoutExerciseEntity>): List<ActiveWorkoutExerciseStep> {
        return workoutExercises.mapNotNull { workoutExercise ->
            val exercise: ExerciseEntity = exerciseRepository.getExercise(workoutExercise.exerciseId) ?: return@mapNotNull null
            ActiveWorkoutExerciseStep(
                workoutExerciseId = workoutExercise.id,
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                imageUrl = exercise.imageUrl,
                targetSets = workoutExercise.targetSets,
                targetReps = workoutExercise.targetRepsMax ?: workoutExercise.targetRepsMin ?: 10,
                restSeconds = workoutExercise.restDurationSeconds ?: 60,
            )
        }
    }
}
