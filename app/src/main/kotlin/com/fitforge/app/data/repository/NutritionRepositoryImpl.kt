package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.FoodItemDao
import com.fitforge.app.data.local.db.dao.MealDao
import com.fitforge.app.data.local.db.dao.NutritionGoalDao
import com.fitforge.app.data.local.db.entity.FoodItemEntity
import com.fitforge.app.data.local.db.entity.MealEntity
import com.fitforge.app.data.local.db.entity.NutritionGoalEntity
import com.fitforge.app.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NutritionRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val foodItemDao: FoodItemDao,
    private val nutritionGoalDao: NutritionGoalDao,
) : NutritionRepository {

    override suspend fun addMeal(meal: MealEntity, foodItems: List<FoodItemEntity>): Result<Unit> = runCatching {
        mealDao.insertMeal(meal)
        if (foodItems.isNotEmpty()) {
            foodItemDao.insertFoodItems(foodItems)
        }
    }

    override suspend fun updateMeal(meal: MealEntity): Result<Unit> = runCatching {
        mealDao.updateMeal(meal)
    }

    override suspend fun deleteMeal(mealId: String): Result<Unit> = runCatching {
        foodItemDao.deleteFoodItemsForMeal(mealId)
        mealDao.deleteMeal(mealId)
    }

    override fun getMealsForDate(userId: String, dateEpochDay: Long): Flow<List<MealEntity>> {
        return mealDao.getMealsForDate(userId, dateEpochDay)
    }

    override fun getMealsForDateRange(
        userId: String,
        startDateEpochDay: Long,
        endDateEpochDay: Long,
    ): Flow<List<MealEntity>> {
        return mealDao.getMealsForDateRange(userId, startDateEpochDay, endDateEpochDay)
    }

    override suspend fun getMealById(mealId: String): MealEntity? {
        return mealDao.getMealById(mealId)
    }

    override fun getFoodItemsForMeal(mealId: String): Flow<List<FoodItemEntity>> {
        return foodItemDao.getFoodItemsForMeal(mealId)
    }

    override fun getTotalCaloriesForDate(userId: String, dateEpochDay: Long): Flow<Int> {
        return mealDao.getTotalCaloriesForDate(userId, dateEpochDay).map { it ?: 0 }
    }

    override fun getTotalProteinForDate(userId: String, dateEpochDay: Long): Flow<Float> {
        return mealDao.getTotalProteinForDate(userId, dateEpochDay).map { it ?: 0f }
    }

    override fun getTotalCarbsForDate(userId: String, dateEpochDay: Long): Flow<Float> {
        return mealDao.getTotalCarbsForDate(userId, dateEpochDay).map { it ?: 0f }
    }

    override fun getTotalFatForDate(userId: String, dateEpochDay: Long): Flow<Float> {
        return mealDao.getTotalFatForDate(userId, dateEpochDay).map { it ?: 0f }
    }

    override suspend fun setNutritionGoal(goal: NutritionGoalEntity): Result<Unit> = runCatching {
        nutritionGoalDao.insertNutritionGoal(goal)
    }

    override fun getNutritionGoal(userId: String): Flow<NutritionGoalEntity?> {
        return nutritionGoalDao.getNutritionGoalForUser(userId)
    }
}
