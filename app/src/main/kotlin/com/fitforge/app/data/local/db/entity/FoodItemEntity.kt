package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItemEntity(
    @PrimaryKey
    val id: String,
    val mealId: String,
    val foodName: String,
    val servingSize: String,
    val servingAmount: Float,
    val calories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatGrams: Float,
    val fiberGrams: Float? = null,
    val sugarGrams: Float? = null,
)
