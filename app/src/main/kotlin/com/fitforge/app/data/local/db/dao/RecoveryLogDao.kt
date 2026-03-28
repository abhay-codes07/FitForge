package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitforge.app.data.local.db.entity.RecoveryLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecoveryLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecoveryLog(log: RecoveryLogEntity)

    @Query("SELECT * FROM recovery_logs WHERE userId = :userId AND logDateEpochDay = :dateEpochDay")
    fun getRecoveryLogForDate(userId: String, dateEpochDay: Long): Flow<RecoveryLogEntity?>

    @Query("SELECT * FROM recovery_logs WHERE userId = :userId AND logDateEpochDay BETWEEN :startDateEpochDay AND :endDateEpochDay ORDER BY logDateEpochDay DESC")
    fun getRecoveryLogsForDateRange(userId: String, startDateEpochDay: Long, endDateEpochDay: Long): Flow<List<RecoveryLogEntity>>

    @Query("SELECT AVG(overallRecoveryScore) FROM recovery_logs WHERE userId = :userId AND logDateEpochDay >= :sinceEpochDay")
    suspend fun getAverageRecoveryScore(userId: String, sinceEpochDay: Long): Float?
}
