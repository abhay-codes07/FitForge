package com.fitforge.app.presentation.workouts

data class ExerciseDetailUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val difficulty: String = "",
    val durationLabel: String = "--",
    val caloriesLabel: String = "--",
    val equipment: List<String> = emptyList(),
    val primaryMuscles: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val errorMessage: String? = null,
)
