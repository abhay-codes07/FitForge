package com.fitforge.app.domain.usecase.active_workout

import com.fitforge.app.data.local.db.entity.ExerciseSetEntity
import com.fitforge.app.domain.repository.ExerciseSetRepository
import java.util.UUID
import javax.inject.Inject

class LogCompletedSetUseCase @Inject constructor(
    private val exerciseSetRepository: ExerciseSetRepository,
) {
    suspend operator fun invoke(
        workoutId: String,
        workoutExerciseId: String,
        exerciseId: String,
        setNumber: Int,
        reps: Int,
        weightKg: Float?,
        restSeconds: Int,
        completedAtEpochMillis: Long = System.currentTimeMillis(),
    ) {
        exerciseSetRepository.upsertExerciseSet(
            ExerciseSetEntity(
                id = UUID.randomUUID().toString(),
                workoutId = workoutId,
                workoutExerciseId = workoutExerciseId,
                exerciseId = exerciseId,
                setNumber = setNumber,
                reps = reps,
                weightKg = weightKg,
                durationSeconds = null,
                distanceMeters = null,
                restDurationSeconds = restSeconds,
                rpe = null,
                isWarmUp = false,
                completedAtEpochMillis = completedAtEpochMillis,
                notes = null,
            ),
        )
    }
}
