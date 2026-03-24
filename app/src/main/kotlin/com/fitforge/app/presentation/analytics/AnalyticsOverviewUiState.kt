package com.fitforge.app.presentation.analytics

data class AnalyticsOverviewUiState(
    val isLoading: Boolean = true,
    val selectedRangeDays: Int = 30,
    val totalSteps: String = "0",
    val totalCalories: String = "0 kcal",
    val totalWorkoutMinutes: String = "0 min",
    val completedWorkouts: String = "0",
    val averageWorkoutMinutes: String = "0 min/day",
    val weightChangeLabel: String = "--",
    val stepTrend: List<Int> = emptyList(),
    val errorMessage: String? = null,
)
