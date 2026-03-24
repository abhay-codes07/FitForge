package com.fitforge.app.domain.usecase.workouts

import com.fitforge.app.data.local.db.entity.ExerciseEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetWorkoutLibraryExercisesUseCase @Inject constructor(
    private val searchExercisesUseCase: SearchExercisesUseCase,
) {
    operator fun invoke(
        query: String,
        selectedCategory: String?,
        selectedDifficulty: String?,
    ): Flow<List<ExerciseEntity>> {
        return searchExercisesUseCase(query).map { exercises ->
            exercises.filter { exercise ->
                val categoryMatch = selectedCategory == null || exercise.category.equals(selectedCategory, ignoreCase = true)
                val difficultyMatch = selectedDifficulty == null || exercise.difficulty.equals(selectedDifficulty, ignoreCase = true)
                categoryMatch && difficultyMatch
            }
        }
    }
}
