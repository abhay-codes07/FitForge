package com.fitforge.app.presentation.active_workout

data class WorkoutSummaryUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val workoutName: String = "",
    val completedAtLabel: String = "",
    val durationLabel: String = "0 min",
    val caloriesLabel: String = "0 kcal",
    val totalSetsLabel: String = "0",
    val totalRepsLabel: String = "0",
    val totalVolumeLabel: String = "0 kg",
    val personalRecordsLabel: String = "0",
    val shareText: String = "",
)
