package com.fitforge.app.domain.usecase.gps_tracking

import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import java.util.UUID
import javax.inject.Inject

class CreateGpsWorkoutSessionUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(mode: String, nowEpochMillis: Long = System.currentTimeMillis()): String {
        val workoutId = UUID.randomUUID().toString()
        val user = userRepository.getPrimaryUser()
        val normalizedMode = mode.trim().lowercase()

        val workout = WorkoutEntity(
            id = workoutId,
            userId = user?.id,
            name = "$mode Session",
            description = "Outdoor $mode activity",
            workoutType = normalizedMode,
            difficulty = "beginner",
            estimatedDurationMinutes = 0,
            estimatedCalories = 0,
            source = "gps_tracking",
            scheduledDateEpochMillis = null,
            completedAtEpochMillis = null,
            status = "in_progress",
            notes = null,
            createdAtEpochMillis = nowEpochMillis,
            updatedAtEpochMillis = nowEpochMillis,
        )

        workoutRepository.upsertWorkout(workout)
        return workoutId
    }
}
