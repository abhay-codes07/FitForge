package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.FoodItemEntity
import com.fitforge.app.data.local.db.entity.MealEntity
import com.fitforge.app.data.local.db.entity.NutritionGoalEntity
import kotlinx.coroutines.flow.Flow

interface NutritionRepository {
    suspend fun addMeal(meal: MealEntity, foodItems: List<FoodItemEntity>): Result<Unit>
    suspend fun updateMeal(meal: MealEntity): Result<Unit>
    suspend fun deleteMeal(mealId: String): Result<Unit>
    fun getMealsForDate(userId: String, dateEpochDay: Long): Flow<List<MealEntity>>
    fun getMealsForDateRange(userId: String, startDateEpochDay: Long, endDateEpochDay: Long): Flow<List<MealEntity>>
    suspend fun getMealById(mealId: String): MealEntity?
    fun getFoodItemsForMeal(mealId: String): Flow<List<FoodItemEntity>>
    fun getTotalCaloriesForDate(userId: String, dateEpochDay: Long): Flow<Int>
    fun getTotalProteinForDate(userId: String, dateEpochDay: Long): Flow<Float>
    fun getTotalCarbsForDate(userId: String, dateEpochDay: Long): Flow<Float>
    fun getTotalFatForDate(userId: String, dateEpochDay: Long): Flow<Float>
    suspend fun setNutritionGoal(goal: NutritionGoalEntity): Result<Unit>
    fun getNutritionGoal(userId: String): Flow<NutritionGoalEntity?>
}
