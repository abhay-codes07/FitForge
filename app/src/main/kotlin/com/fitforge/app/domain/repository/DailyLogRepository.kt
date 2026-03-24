package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.DailyLogEntity
import kotlinx.coroutines.flow.Flow

interface DailyLogRepository {
    fun observeLogForDay(userId: String, epochDay: Long): Flow<DailyLogEntity?>
    suspend fun getLogForDay(userId: String, epochDay: Long): DailyLogEntity?
    fun observeLogsInRange(
        userId: String,
        startEpochDay: Long,
        endEpochDay: Long,
    ): Flow<List<DailyLogEntity>>
    suspend fun getLogsInRange(
        userId: String,
        startEpochDay: Long,
        endEpochDay: Long,
    ): List<DailyLogEntity>
    fun observeLatestLog(userId: String): Flow<DailyLogEntity?>
    suspend fun upsertLog(log: DailyLogEntity)
    suspend fun upsertLogs(logs: List<DailyLogEntity>)
    suspend fun deleteLog(log: DailyLogEntity)
}
