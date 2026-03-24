package com.fitforge.app.domain.usecase.workouts

import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity
import com.fitforge.app.domain.repository.UserRepository
import java.util.UUID
import javax.inject.Inject

data class CreateCustomWorkoutDraft(
    val name: String,
    val description: String,
    val workoutType: String,
    val difficulty: String,
    val estimatedDurationMinutes: Int,
    val selectedExerciseIds: List<String>,
)

class BuildAndCreateCustomWorkoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val createCustomWorkoutUseCase: CreateCustomWorkoutUseCase,
) {
    suspend operator fun invoke(draft: CreateCustomWorkoutDraft): String {
        require(draft.name.isNotBlank()) { "Workout name is required" }
        require(draft.selectedExerciseIds.isNotEmpty()) { "Select at least one exercise" }

        val now = System.currentTimeMillis()
        val workoutId = UUID.randomUUID().toString()
        val userId = userRepository.getPrimaryUser()?.id

        val workout = WorkoutEntity(
            id = workoutId,
            userId = userId,
            name = draft.name.trim(),
            description = draft.description.trim(),
            workoutType = draft.workoutType,
            difficulty = draft.difficulty,
            estimatedDurationMinutes = draft.estimatedDurationMinutes,
            estimatedCalories = null,
            source = "custom",
            scheduledDateEpochMillis = null,
            completedAtEpochMillis = null,
            status = "planned",
            notes = null,
            createdAtEpochMillis = now,
            updatedAtEpochMillis = now,
        )

        val workoutExercises = draft.selectedExerciseIds.distinct().mapIndexed { index, exerciseId ->
            WorkoutExerciseEntity(
                id = UUID.randomUUID().toString(),
                workoutId = workoutId,
                exerciseId = exerciseId,
                sequenceIndex = index,
                targetSets = 3,
                targetRepsMin = 8,
                targetRepsMax = 12,
                targetDurationSeconds = null,
                targetDistanceMeters = null,
                targetWeightKg = null,
                restDurationSeconds = 90,
                supersetGroup = null,
                notes = null,
                isOptional = false,
            )
        }

        createCustomWorkoutUseCase(
            CreateCustomWorkoutRequest(
                workout = workout,
                workoutExercises = workoutExercises,
            ),
        )

        return workoutId
    }
}
