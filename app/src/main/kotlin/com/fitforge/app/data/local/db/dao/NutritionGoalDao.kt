package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitforge.app.data.local.db.entity.NutritionGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutritionGoal(goal: NutritionGoalEntity)

    @Update
    suspend fun updateNutritionGoal(goal: NutritionGoalEntity)

    @Query("SELECT * FROM nutrition_goals WHERE userId = :userId")
    fun getNutritionGoalForUser(userId: String): Flow<NutritionGoalEntity?>

    @Query("SELECT * FROM nutrition_goals WHERE userId = :userId")
    suspend fun getNutritionGoalForUserOnce(userId: String): NutritionGoalEntity?
}
