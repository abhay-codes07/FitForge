package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val mealType: String, // breakfast, lunch, dinner, snack
    val mealName: String,
    val totalCalories: Int,
    val totalProteinGrams: Float,
    val totalCarbsGrams: Float,
    val totalFatGrams: Float,
    val mealDateEpochDay: Long,
    val mealTimeEpochMillis: Long,
    val notes: String? = null,
    val photoUrl: String? = null,
    val createdAtEpochMillis: Long,
)
