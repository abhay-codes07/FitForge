package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fitforge.app.data.local.db.entity.PersonalRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalRecordDao {
    @Query("SELECT * FROM personal_records WHERE userId = :userId ORDER BY achievedAtEpochMillis DESC")
    fun observeRecordsForUser(userId: String): Flow<List<PersonalRecordEntity>>

    @Query(
        """
        SELECT * FROM personal_records
        WHERE userId = :userId
          AND exerciseId = :exerciseId
        ORDER BY achievedAtEpochMillis DESC
        """,
    )
    fun observeRecordsForExercise(userId: String, exerciseId: String): Flow<List<PersonalRecordEntity>>

    @Query(
        """
        SELECT * FROM personal_records
        WHERE userId = :userId
          AND metricType = :metricType
        ORDER BY value DESC, achievedAtEpochMillis DESC
        LIMIT 1
        """,
    )
    fun observeTopRecordForMetric(userId: String, metricType: String): Flow<PersonalRecordEntity?>

    @Query(
        """
        SELECT * FROM personal_records
        WHERE userId = :userId
          AND achievedAtEpochMillis BETWEEN :startEpochMillis AND :endEpochMillis
        ORDER BY achievedAtEpochMillis ASC
        """,
    )
    fun observeRecordsInRange(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<List<PersonalRecordEntity>>

    @Upsert
    suspend fun upsert(record: PersonalRecordEntity)

    @Upsert
    suspend fun upsertAll(records: List<PersonalRecordEntity>)

    @Delete
    suspend fun delete(record: PersonalRecordEntity)
}
