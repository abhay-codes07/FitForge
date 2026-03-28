package com.fitforge.app.presentation.dailylog

data class DailyLogUiState(
    val isLoading: Boolean = true,
    val steps: Int = 0,
    val waterIntakeMl: Int = 0,
    val waterGoalMl: Int = 2000,
    val sleepMinutes: Int? = null,
    val sleepGoalMinutes: Int = 480, // 8 hours
    val activeCalories: Int = 0,
    val workoutMinutes: Int = 0,
    val distanceKm: String = "0.0",
    val restingHeartRate: Int? = null,
    val averageHeartRate: Int? = null,
    val readinessScore: Int? = null,
    val notes: String? = null,
    val errorMessage: String? = null,
)
