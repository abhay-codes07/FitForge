package com.fitforge.app.domain.usecase.recovery

import com.fitforge.app.data.local.db.dao.RecoveryLogDao
import com.fitforge.app.data.local.db.entity.RecoveryLogEntity
import com.fitforge.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class LogRecoveryUseCase @Inject constructor(
    private val recoveryLogDao: RecoveryLogDao,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        recoveryScore: Int,
        sleepQuality: Int,
        sleepHours: Float,
        stressLevel: Int,
        fatigueLevel: Int,
        restingHeartRate: Int?,
        sorenessAreas: List<String>,
        notes: String?,
    ): Result<Unit> = runCatching {
        val currentUser = userRepository.getPrimaryUser()
            ?: throw IllegalStateException("User not logged in")

        val log = RecoveryLogEntity(
            id = UUID.randomUUID().toString(),
            userId = currentUser.id,
            logDateEpochDay = LocalDate.now().toEpochDay(),
            overallRecoveryScore = recoveryScore,
            sleepQuality = sleepQuality,
            sleepHours = sleepHours,
            stressLevel = stressLevel,
            fatigueLevel = fatigueLevel,
            restingHeartRate = restingHeartRate,
            sorenessAreas = sorenessAreas.joinToString(","),
            notes = notes,
            createdAtEpochMillis = System.currentTimeMillis(),
        )

        recoveryLogDao.insertRecoveryLog(log)
    }
}

class GetTodayRecoveryLogUseCase @Inject constructor(
    private val recoveryLogDao: RecoveryLogDao,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<RecoveryLogEntity?> {
        val currentUser = userRepository.getPrimaryUser() ?: return flowOf(null)
        val todayEpochDay = LocalDate.now().toEpochDay()
        return recoveryLogDao.getRecoveryLogForDate(currentUser.id, todayEpochDay)
    }
}

class GetRecoveryHistoryUseCase @Inject constructor(
    private val recoveryLogDao: RecoveryLogDao,
    private val userRepository: UserRepository,
) {
    operator fun invoke(daysBack: Int = 7): Flow<List<RecoveryLogEntity>> {
        val currentUser = userRepository.getPrimaryUser() ?: return flowOf(emptyList())
        val endDate = LocalDate.now().toEpochDay()
        val startDate = endDate - daysBack
        return recoveryLogDao.getRecoveryLogsForDateRange(currentUser.id, startDate, endDate)
    }
}

class GetAverageRecoveryScoreUseCase @Inject constructor(
    private val recoveryLogDao: RecoveryLogDao,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(daysBack: Int = 7): Float {
        val currentUser = userRepository.getPrimaryUser() ?: return 0f
        val sinceEpochDay = LocalDate.now().toEpochDay() - daysBack
        return recoveryLogDao.getAverageRecoveryScore(currentUser.id, sinceEpochDay) ?: 0f
    }
}
