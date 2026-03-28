package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_goals")
data class NutritionGoalEntity(
    @PrimaryKey
    val userId: String,
    val dailyCalorieGoal: Int,
    val dailyProteinGoal: Float,
    val dailyCarbsGoal: Float,
    val dailyFatGoal: Float,
    val dailyWaterGoalMl: Int = 2000,
    val updatedAtEpochMillis: Long,
)
