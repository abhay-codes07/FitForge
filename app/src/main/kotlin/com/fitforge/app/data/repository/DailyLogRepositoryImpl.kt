package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.DailyLogDao
import com.fitforge.app.data.local.db.entity.DailyLogEntity
import com.fitforge.app.domain.repository.DailyLogRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DailyLogRepositoryImpl @Inject constructor(
    private val dailyLogDao: DailyLogDao,
) : DailyLogRepository {
    override fun observeLogForDay(userId: String, epochDay: Long): Flow<DailyLogEntity?> = dailyLogDao.observeLogForDay(userId, epochDay)

    override suspend fun getLogForDay(userId: String, epochDay: Long): DailyLogEntity? = dailyLogDao.getLogForDay(userId, epochDay)

    override fun observeLogsInRange(
        userId: String,
        startEpochDay: Long,
        endEpochDay: Long,
    ): Flow<List<DailyLogEntity>> = dailyLogDao.observeLogsInRange(userId, startEpochDay, endEpochDay)

    override suspend fun getLogsInRange(
        userId: String,
        startEpochDay: Long,
        endEpochDay: Long,
    ): List<DailyLogEntity> = dailyLogDao.getLogsInRange(userId, startEpochDay, endEpochDay)

    override fun observeLatestLog(userId: String): Flow<DailyLogEntity?> = dailyLogDao.observeLatestLog(userId)

    override suspend fun upsertLog(log: DailyLogEntity) = dailyLogDao.upsert(log)

    override suspend fun upsertLogs(logs: List<DailyLogEntity>) = dailyLogDao.upsertAll(logs)

    override suspend fun deleteLog(log: DailyLogEntity) = dailyLogDao.delete(log)
}
