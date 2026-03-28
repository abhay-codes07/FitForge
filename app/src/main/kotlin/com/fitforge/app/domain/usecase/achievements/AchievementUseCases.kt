package com.fitforge.app.domain.usecase.achievements

import com.fitforge.app.data.local.db.entity.AchievementEntity
import com.fitforge.app.domain.repository.AchievementRepository
import com.fitforge.app.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf

class GetAchievementsUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<AchievementEntity>> {
        val user = userRepository.observePrimaryUser()
        return user.firstOrNull()?.let { u ->
            achievementRepository.observeAchievementsForUser(u.id)
        } ?: flowOf(emptyList())
    }

    operator fun invoke(category: String): Flow<List<AchievementEntity>> {
        val user = userRepository.observePrimaryUser()
        return user.firstOrNull()?.let { u ->
            achievementRepository.observeAchievementsByCategory(u.id, category)
        } ?: flowOf(emptyList())
    }
}

class GetUnlockedAchievementsUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<AchievementEntity>> {
        val user = userRepository.observePrimaryUser()
        return user.firstOrNull()?.let { u ->
            achievementRepository.observeUnlockedAchievements(u.id)
        } ?: flowOf(emptyList())
    }
}

class UnlockAchievementUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
) {
    suspend operator fun invoke(achievement: AchievementEntity): Result<Unit> {
        return runCatching {
            if (achievement.progress >= achievement.target && achievement.unlockedAtEpochMillis == null) {
                val unlocked = achievement.copy(
                    unlockedAtEpochMillis = System.currentTimeMillis(),
                )
                achievementRepository.upsertAchievement(unlocked)
            }
        }
    }
}

class ClaimAchievementUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
) {
    suspend operator fun invoke(achievement: AchievementEntity): Result<Unit> {
        return runCatching {
            if (achievement.unlockedAtEpochMillis != null && !achievement.isClaimed) {
                val claimed = achievement.copy(isClaimed = true)
                achievementRepository.upsertAchievement(claimed)
            }
        }
    }
}

class UpdateAchievementProgressUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val unlockAchievementUseCase: UnlockAchievementUseCase,
) {
    suspend operator fun invoke(
        achievementId: String,
        newProgress: Float,
        autoUnlock: Boolean = true,
    ): Result<Unit> {
        return runCatching {
            val achievements = achievementRepository.observeAchievementsForUser("")
                .firstOrNull() ?: emptyList()

            val achievement = achievements.find { it.id == achievementId } ?: return@runCatching

            val updated = achievement.copy(progress = newProgress.coerceAtMost(achievement.target))
            achievementRepository.upsertAchievement(updated)

            if (autoUnlock && updated.progress >= updated.target) {
                unlockAchievementUseCase(updated)
            }
        }
    }
}

class InitializeAchievementsUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return runCatching {
            val user = userRepository.getPrimaryUser() ?: return@runCatching

            val defaultAchievements = listOf(
                AchievementEntity(
                    id = "ach_first_workout",
                    userId = user.id,
                    title = "First Workout",
                    description = "Complete your first workout",
                    category = "workouts",
                    iconName = "trophy",
                    progress = 0f,
                    target = 1f,
                    unlockedAtEpochMillis = null,
                    isClaimed = false,
                ),
                AchievementEntity(
                    id = "ach_10_workouts",
                    userId = user.id,
                    title = "Dedicated",
                    description = "Complete 10 workouts",
                    category = "workouts",
                    iconName = "medal",
                    progress = 0f,
                    target = 10f,
                    unlockedAtEpochMillis = null,
                    isClaimed = false,
                ),
                AchievementEntity(
                    id = "ach_50_workouts",
                    userId = user.id,
                    title = "Committed",
                    description = "Complete 50 workouts",
                    category = "workouts",
                    iconName = "star",
                    progress = 0f,
                    target = 50f,
                    unlockedAtEpochMillis = null,
                    isClaimed = false,
                ),
                AchievementEntity(
                    id = "ach_7_day_streak",
                    userId = user.id,
                    title = "Week Warrior",
                    description = "Work out 7 days in a row",
                    category = "streaks",
                    iconName = "fire",
                    progress = 0f,
                    target = 7f,
                    unlockedAtEpochMillis = null,
                    isClaimed = false,
                ),
                AchievementEntity(
                    id = "ach_30_day_streak",
                    userId = user.id,
                    title = "Month Master",
                    description = "Work out 30 days in a row",
                    category = "streaks",
                    iconName = "flame",
                    progress = 0f,
                    target = 30f,
                    unlockedAtEpochMillis = null,
                    isClaimed = false,
                ),
                AchievementEntity(
                    id = "ach_100kg_lifted",
                    userId = user.id,
                    title = "Century Lifter",
                    description = "Lift 100kg total in a single workout",
                    category = "strength",
                    iconName = "dumbbell",
                    progress = 0f,
                    target = 100f,
                    unlockedAtEpochMillis = null,
                    isClaimed = false,
                ),
                AchievementEntity(
                    id = "ach_5km_run",
                    userId = user.id,
                    title = "5K Runner",
                    description = "Run 5 kilometers in a single session",
                    category = "cardio",
                    iconName = "running",
                    progress = 0f,
                    target = 5000f,
                    unlockedAtEpochMillis = null,
                    isClaimed = false,
                ),
                AchievementEntity(
                    id = "ach_10_body_measurements",
                    userId = user.id,
                    title = "Progress Tracker",
                    description = "Log 10 body measurements",
                    category = "tracking",
                    iconName = "chart",
                    progress = 0f,
                    target = 10f,
                    unlockedAtEpochMillis = null,
                    isClaimed = false,
                ),
            )

            achievementRepository.upsertAchievements(defaultAchievements)
        }
    }
}
