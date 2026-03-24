package com.fitforge.app.domain.usecase.workouts

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class ExerciseDetailData(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val durationLabel: String,
    val caloriesLabel: String,
    val equipment: List<String>,
    val primaryMuscles: List<String>,
    val instructions: List<String>,
)

class GetExerciseDetailUseCase @Inject constructor(
    private val observeExerciseDetailUseCase: ObserveExerciseDetailUseCase,
) {
    operator fun invoke(exerciseId: String): Flow<ExerciseDetailData?> {
        return observeExerciseDetailUseCase(exerciseId).map { exercise ->
            exercise?.let {
                ExerciseDetailData(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    category = it.category,
                    difficulty = it.difficulty,
                    durationLabel = it.estimatedDurationSeconds?.let { seconds -> "${seconds / 60} min" } ?: "--",
                    caloriesLabel = it.estimatedCalories?.let { calories -> "$calories kcal" } ?: "--",
                    equipment = it.equipment.toList().sorted(),
                    primaryMuscles = it.primaryMuscles.toList().sorted(),
                    instructions = it.instructions,
                )
            }
        }
    }
}
