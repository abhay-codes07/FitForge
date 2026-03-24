package com.fitforge.app.presentation.home

data class HomeDashboardUiState(
    val isLoading: Boolean = true,
    val isGuest: Boolean = false,
    val greetingName: String = "Athlete",
    val steps: Int = 0,
    val activeCalories: Int = 0,
    val workoutMinutes: Int = 0,
    val waterIntakeMl: Int = 0,
    val latestWeightLabel: String = "-",
    val nextWorkoutTitle: String = "No workout scheduled",
    val recentWorkoutTitles: List<String> = emptyList(),
    val errorMessage: String? = null,
)
