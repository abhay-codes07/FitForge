package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitforge.app.data.local.db.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<MealEntity>)

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Query("SELECT * FROM meals WHERE userId = :userId AND mealDateEpochDay = :dateEpochDay ORDER BY mealTimeEpochMillis ASC")
    fun getMealsForDate(userId: String, dateEpochDay: Long): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE userId = :userId AND mealDateEpochDay BETWEEN :startDateEpochDay AND :endDateEpochDay ORDER BY mealDateEpochDay DESC, mealTimeEpochMillis DESC")
    fun getMealsForDateRange(userId: String, startDateEpochDay: Long, endDateEpochDay: Long): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE id = :mealId")
    suspend fun getMealById(mealId: String): MealEntity?

    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteMeal(mealId: String)

    @Query("SELECT SUM(totalCalories) FROM meals WHERE userId = :userId AND mealDateEpochDay = :dateEpochDay")
    fun getTotalCaloriesForDate(userId: String, dateEpochDay: Long): Flow<Int?>

    @Query("SELECT SUM(totalProteinGrams) FROM meals WHERE userId = :userId AND mealDateEpochDay = :dateEpochDay")
    fun getTotalProteinForDate(userId: String, dateEpochDay: Long): Flow<Float?>

    @Query("SELECT SUM(totalCarbsGrams) FROM meals WHERE userId = :userId AND mealDateEpochDay = :dateEpochDay")
    fun getTotalCarbsForDate(userId: String, dateEpochDay: Long): Flow<Float?>

    @Query("SELECT SUM(totalFatGrams) FROM meals WHERE userId = :userId AND mealDateEpochDay = :dateEpochDay")
    fun getTotalFatForDate(userId: String, dateEpochDay: Long): Flow<Float?>
}
