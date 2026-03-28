package com.fitforge.app.presentation.nutrition

import com.fitforge.app.data.local.db.entity.MealEntity
import com.fitforge.app.domain.usecase.nutrition.DailyNutritionSummary

data class NutritionUiState(
    val isLoading: Boolean = true,
    val meals: List<MealEntity> = emptyList(),
    val summary: DailyNutritionSummary = DailyNutritionSummary(
        totalCalories = 0,
        totalProtein = 0f,
        totalCarbs = 0f,
        totalFat = 0f,
        calorieGoal = 2000,
        proteinGoal = 150f,
        carbsGoal = 200f,
        fatGoal = 65f,
    ),
    val selectedDateEpochDay: Long = 0L,
    val errorMessage: String? = null,
)
