package com.fitforge.app.domain.usecase.gps_tracking

import com.fitforge.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class CompleteGpsWorkoutSessionUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(
        workoutId: String,
        durationSeconds: Long,
        distanceMeters: Float,
        nowEpochMillis: Long = System.currentTimeMillis(),
    ) {
        val workout = workoutRepository.getWorkout(workoutId) ?: return
        val durationMinutes = (durationSeconds / 60L).toInt().coerceAtLeast(1)
        val distanceKm = distanceMeters / 1000f
        val notes = "Distance: %.2f km".format(distanceKm)

        workoutRepository.upsertWorkout(
            workout.copy(
                estimatedDurationMinutes = durationMinutes,
                completedAtEpochMillis = nowEpochMillis,
                status = "completed",
                notes = notes,
                updatedAtEpochMillis = nowEpochMillis,
            ),
        )
    }
}
