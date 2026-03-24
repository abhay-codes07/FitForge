package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fitforge.app.data.local.db.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements WHERE userId = :userId ORDER BY COALESCE(unlockedAtEpochMillis, 0) DESC, title COLLATE NOCASE ASC")
    fun observeAchievementsForUser(userId: String): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE userId = :userId AND unlockedAtEpochMillis IS NOT NULL ORDER BY unlockedAtEpochMillis DESC")
    fun observeUnlockedAchievements(userId: String): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE userId = :userId AND category = :category ORDER BY progress DESC, title COLLATE NOCASE ASC")
    fun observeAchievementsByCategory(userId: String, category: String): Flow<List<AchievementEntity>>

    @Upsert
    suspend fun upsert(achievement: AchievementEntity)

    @Upsert
    suspend fun upsertAll(achievements: List<AchievementEntity>)

    @Delete
    suspend fun delete(achievement: AchievementEntity)
}
