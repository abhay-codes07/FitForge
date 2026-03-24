package com.fitforge.app.presentation.workouts

data class WorkoutLibraryExerciseItem(
    val id: String,
    val name: String,
    val category: String,
    val difficulty: String,
    val durationLabel: String,
    val caloriesLabel: String,
    val equipment: List<String>,
)

data class WorkoutLibraryUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val selectedDifficulty: String? = null,
    val categoryFilters: List<String> = emptyList(),
    val difficultyFilters: List<String> = emptyList(),
    val exercises: List<WorkoutLibraryExerciseItem> = emptyList(),
    val errorMessage: String? = null,
)
