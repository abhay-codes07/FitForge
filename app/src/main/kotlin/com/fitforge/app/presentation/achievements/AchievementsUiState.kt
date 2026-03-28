package com.fitforge.app.presentation.achievements

import com.fitforge.app.data.local.db.entity.AchievementEntity

data class AchievementsUiState(
    val isLoading: Boolean = true,
    val achievements: List<AchievementEntity> = emptyList(),
    val selectedCategory: String = "all",
    val categories: List<String> = listOf("all", "workouts", "streaks", "strength", "cardio", "tracking"),
    val errorMessage: String? = null,
)
