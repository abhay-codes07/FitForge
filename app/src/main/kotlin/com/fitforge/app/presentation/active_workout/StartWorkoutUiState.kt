package com.fitforge.app.presentation.active_workout

data class StartWorkoutUiState(
    val isLoading: Boolean = true,
    val options: List<String> = emptyList(),
    val scheduledWorkoutCountLabel: String = "",
    val selectedOption: String? = null,
    val errorMessage: String? = null,
)
