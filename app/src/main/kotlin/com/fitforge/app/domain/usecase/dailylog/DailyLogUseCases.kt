package com.fitforge.app.domain.usecase.dailylog

import com.fitforge.app.data.local.db.entity.DailyLogEntity
import com.fitforge.app.domain.repository.DailyLogRepository
import com.fitforge.app.domain.repository.UserRepository
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetTodayLogUseCase @Inject constructor(
    private val dailyLogRepository: DailyLogRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<DailyLogEntity?> {
        val user = userRepository.getPrimaryUser() ?: return flowOf(null)
        val today = LocalDate.now().toEpochDay()
        return dailyLogRepository.observeLogForDay(user.id, today)
    }
}

class GetLogForDateUseCase @Inject constructor(
    private val dailyLogRepository: DailyLogRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(date: LocalDate): Flow<DailyLogEntity?> {
        val user = userRepository.getPrimaryUser() ?: return flowOf(null)
        return dailyLogRepository.observeLogForDay(user.id, date.toEpochDay())
    }
}

class GetWeekLogsUseCase @Inject constructor(
    private val dailyLogRepository: DailyLogRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<DailyLogEntity>> {
        val user = userRepository.getPrimaryUser() ?: return flowOf(emptyList())
        val today = LocalDate.now().toEpochDay()
        val weekAgo = today - 7
        return dailyLogRepository.observeLogsInRange(user.id, weekAgo, today)
    }
}

class UpdateWaterIntakeUseCase @Inject constructor(
    private val dailyLogRepository: DailyLogRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(waterMl: Int): Result<Unit> {
        return runCatching {
            val user = userRepository.getPrimaryUser() ?: error("User not found")
            val today = LocalDate.now().toEpochDay()
            val log = dailyLogRepository.getLogForDay(user.id, today)

            if (log != null) {
                dailyLogRepository.upsertLog(log.copy(waterIntakeMl = waterMl))
            } else {
                dailyLogRepository.upsertLog(
                    DailyLogEntity(
                        id = UUID.randomUUID().toString(),
                        userId = user.id,
                        logDateEpochDay = today,
                        steps = 0,
                        activeCalories = 0,
                        workoutMinutes = 0,
                        waterIntakeMl = waterMl,
                        sleepMinutes = null,
                        distanceMeters = 0f,
                        restingHeartRate = null,
                        averageHeartRate = null,
                        readinessScore = null,
                        notes = null,
                    ),
                )
            }
        }
    }
}

class UpdateSleepUseCase @Inject constructor(
    private val dailyLogRepository: DailyLogRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(sleepMinutes: Int): Result<Unit> {
        return runCatching {
            val user = userRepository.getPrimaryUser() ?: error("User not found")
            val today = LocalDate.now().toEpochDay()
            val log = dailyLogRepository.getLogForDay(user.id, today)

            if (log != null) {
                dailyLogRepository.upsertLog(log.copy(sleepMinutes = sleepMinutes))
            } else {
                dailyLogRepository.upsertLog(
                    DailyLogEntity(
                        id = UUID.randomUUID().toString(),
                        userId = user.id,
                        logDateEpochDay = today,
                        steps = 0,
                        activeCalories = 0,
                        workoutMinutes = 0,
                        waterIntakeMl = 0,
                        sleepMinutes = sleepMinutes,
                        distanceMeters = 0f,
                        restingHeartRate = null,
                        averageHeartRate = null,
                        readinessScore = null,
                        notes = null,
                    ),
                )
            }
        }
    }
}

class UpdateStepsUseCase @Inject constructor(
    private val dailyLogRepository: DailyLogRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(steps: Int): Result<Unit> {
        return runCatching {
            val user = userRepository.getPrimaryUser() ?: error("User not found")
            val today = LocalDate.now().toEpochDay()
            val log = dailyLogRepository.getLogForDay(user.id, today)

            if (log != null) {
                dailyLogRepository.upsertLog(log.copy(steps = steps))
            } else {
                dailyLogRepository.upsertLog(
                    DailyLogEntity(
                        id = UUID.randomUUID().toString(),
                        userId = user.id,
                        logDateEpochDay = today,
                        steps = steps,
                        activeCalories = 0,
                        workoutMinutes = 0,
                        waterIntakeMl = 0,
                        sleepMinutes = null,
                        distanceMeters = 0f,
                        restingHeartRate = null,
                        averageHeartRate = null,
                        readinessScore = null,
                        notes = null,
                    ),
                )
            }
        }
    }
}

class AddWaterIntakeUseCase @Inject constructor(
    private val dailyLogRepository: DailyLogRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(addMl: Int): Result<Unit> {
        return runCatching {
            val user = userRepository.getPrimaryUser() ?: error("User not found")
            val today = LocalDate.now().toEpochDay()
            val log = dailyLogRepository.getLogForDay(user.id, today)

            if (log != null) {
                dailyLogRepository.upsertLog(log.copy(waterIntakeMl = log.waterIntakeMl + addMl))
            } else {
                dailyLogRepository.upsertLog(
                    DailyLogEntity(
                        id = UUID.randomUUID().toString(),
                        userId = user.id,
                        logDateEpochDay = today,
                        steps = 0,
                        activeCalories = 0,
                        workoutMinutes = 0,
                        waterIntakeMl = addMl,
                        sleepMinutes = null,
                        distanceMeters = 0f,
                        restingHeartRate = null,
                        averageHeartRate = null,
                        readinessScore = null,
                        notes = null,
                    ),
                )
            }
        }
    }
}
