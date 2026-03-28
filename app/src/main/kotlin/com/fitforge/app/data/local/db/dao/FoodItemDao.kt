package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitforge.app.data.local.db.entity.FoodItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(foodItem: FoodItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItems(foodItems: List<FoodItemEntity>)

    @Query("SELECT * FROM food_items WHERE mealId = :mealId")
    fun getFoodItemsForMeal(mealId: String): Flow<List<FoodItemEntity>>

    @Query("DELETE FROM food_items WHERE id = :foodItemId")
    suspend fun deleteFoodItem(foodItemId: String)

    @Query("DELETE FROM food_items WHERE mealId = :mealId")
    suspend fun deleteFoodItemsForMeal(mealId: String)
}
