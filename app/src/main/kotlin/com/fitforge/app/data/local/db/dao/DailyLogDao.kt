package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fitforge.app.data.local.db.entity.DailyLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs WHERE userId = :userId AND logDateEpochDay = :epochDay LIMIT 1")
    fun observeLogForDay(userId: String, epochDay: Long): Flow<DailyLogEntity?>

    @Query("SELECT * FROM daily_logs WHERE userId = :userId AND logDateEpochDay = :epochDay LIMIT 1")
    suspend fun getLogForDay(userId: String, epochDay: Long): DailyLogEntity?

    @Query(
        """
        SELECT * FROM daily_logs
        WHERE userId = :userId
          AND logDateEpochDay BETWEEN :startEpochDay AND :endEpochDay
        ORDER BY logDateEpochDay ASC
        """,
    )
    fun observeLogsInRange(
        userId: String,
        startEpochDay: Long,
        endEpochDay: Long,
    ): Flow<List<DailyLogEntity>>

    @Query(
        """
        SELECT * FROM daily_logs
        WHERE userId = :userId
          AND logDateEpochDay BETWEEN :startEpochDay AND :endEpochDay
        ORDER BY logDateEpochDay ASC
        """,
    )
    suspend fun getLogsInRange(
        userId: String,
        startEpochDay: Long,
        endEpochDay: Long,
    ): List<DailyLogEntity>

    @Query("SELECT * FROM daily_logs WHERE userId = :userId ORDER BY logDateEpochDay DESC LIMIT 1")
    fun observeLatestLog(userId: String): Flow<DailyLogEntity?>

    @Upsert
    suspend fun upsert(log: DailyLogEntity)

    @Upsert
    suspend fun upsertAll(logs: List<DailyLogEntity>)

    @Delete
    suspend fun delete(log: DailyLogEntity)
}
