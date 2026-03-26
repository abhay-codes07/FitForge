package com.fitforge.app.domain.usecase.active_workout

import com.fitforge.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class CompleteWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(
        workoutId: String,
        nowEpochMillis: Long = System.currentTimeMillis(),
    ) {
        val workout = workoutRepository.getWorkout(workoutId) ?: error("Workout not found")
        workoutRepository.upsertWorkout(
            workout.copy(
                status = "completed",
                completedAtEpochMillis = nowEpochMillis,
                updatedAtEpochMillis = nowEpochMillis,
            ),
        )
    }
}
