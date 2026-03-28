package com.fitforge.app.domain.usecase.active_workout

import com.fitforge.app.domain.repository.WorkoutRepository
import com.fitforge.app.domain.usecase.sync.PushWorkoutToCloudUseCase
import javax.inject.Inject

class CompleteWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val pushWorkoutToCloudUseCase: PushWorkoutToCloudUseCase,
) {
    suspend operator fun invoke(
        workoutId: String,
        nowEpochMillis: Long = System.currentTimeMillis(),
    ) {
        val workout = workoutRepository.getWorkout(workoutId) ?: error("Workout not found")
        val completedWorkout = workout.copy(
            status = "completed",
            completedAtEpochMillis = nowEpochMillis,
            updatedAtEpochMillis = nowEpochMillis,
        )
        workoutRepository.upsertWorkout(completedWorkout)
        pushWorkoutToCloudUseCase(completedWorkout)
    }
}
