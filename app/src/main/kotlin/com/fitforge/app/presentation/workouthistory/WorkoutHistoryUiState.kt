package com.fitforge.app.presentation.workouthistory

import com.fitforge.app.data.local.db.entity.WorkoutEntity

data class WorkoutHistoryUiState(
    val isLoading: Boolean = true,
    val workouts: List<WorkoutEntity> = emptyList(),
    val currentStreak: Int = 0,
    val totalWorkouts: Int = 0,
    val totalMinutes: Int = 0,
    val averageDuration: Int = 0,
    val favoriteType: String = "",
    val errorMessage: String? = null,
)
