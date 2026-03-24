package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.AchievementDao
import com.fitforge.app.data.local.db.entity.AchievementEntity
import com.fitforge.app.domain.repository.AchievementRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: AchievementDao,
) : AchievementRepository {
    override fun observeAchievementsForUser(userId: String): Flow<List<AchievementEntity>> = achievementDao.observeAchievementsForUser(userId)

    override fun observeUnlockedAchievements(userId: String): Flow<List<AchievementEntity>> = achievementDao.observeUnlockedAchievements(userId)

    override fun observeAchievementsByCategory(userId: String, category: String): Flow<List<AchievementEntity>> =
        achievementDao.observeAchievementsByCategory(userId, category)

    override suspend fun upsertAchievement(achievement: AchievementEntity) = achievementDao.upsert(achievement)

    override suspend fun upsertAchievements(achievements: List<AchievementEntity>) = achievementDao.upsertAll(achievements)

    override suspend fun deleteAchievement(achievement: AchievementEntity) = achievementDao.delete(achievement)
}
