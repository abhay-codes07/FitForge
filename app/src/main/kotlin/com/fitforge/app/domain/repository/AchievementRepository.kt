package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun observeAchievementsForUser(userId: String): Flow<List<AchievementEntity>>
    fun observeUnlockedAchievements(userId: String): Flow<List<AchievementEntity>>
    fun observeAchievementsByCategory(userId: String, category: String): Flow<List<AchievementEntity>>
    suspend fun upsertAchievement(achievement: AchievementEntity)
    suspend fun upsertAchievements(achievements: List<AchievementEntity>)
    suspend fun deleteAchievement(achievement: AchievementEntity)
}
