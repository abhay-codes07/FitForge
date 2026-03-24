package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.PersonalRecordEntity
import kotlinx.coroutines.flow.Flow

interface PersonalRecordRepository {
    fun observeRecordsForUser(userId: String): Flow<List<PersonalRecordEntity>>
    fun observeRecordsForExercise(userId: String, exerciseId: String): Flow<List<PersonalRecordEntity>>
    fun observeTopRecordForMetric(userId: String, metricType: String): Flow<PersonalRecordEntity?>
    fun observeRecordsInRange(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<List<PersonalRecordEntity>>
    suspend fun upsertRecord(record: PersonalRecordEntity)
    suspend fun upsertRecords(records: List<PersonalRecordEntity>)
    suspend fun deleteRecord(record: PersonalRecordEntity)
}
