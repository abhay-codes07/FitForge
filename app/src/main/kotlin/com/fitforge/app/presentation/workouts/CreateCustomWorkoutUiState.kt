package com.fitforge.app.presentation.workouts

data class CustomWorkoutExerciseItem(
    val id: String,
    val name: String,
    val category: String,
)

data class CreateCustomWorkoutUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val name: String = "",
    val description: String = "",
    val searchQuery: String = "",
    val selectedWorkoutType: String = "Strength",
    val selectedDifficulty: String = "Beginner",
    val durationMinutes: String = "30",
    val workoutTypeOptions: List<String> = listOf("Strength", "Cardio", "Mobility"),
    val difficultyOptions: List<String> = listOf("Beginner", "Intermediate", "Advanced"),
    val exercises: List<CustomWorkoutExerciseItem> = emptyList(),
    val selectedExerciseIds: Set<String> = emptySet(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
)
