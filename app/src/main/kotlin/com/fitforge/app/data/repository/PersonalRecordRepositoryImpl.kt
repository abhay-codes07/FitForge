package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.PersonalRecordDao
import com.fitforge.app.data.local.db.entity.PersonalRecordEntity
import com.fitforge.app.domain.repository.PersonalRecordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class PersonalRecordRepositoryImpl @Inject constructor(
    private val personalRecordDao: PersonalRecordDao,
) : PersonalRecordRepository {
    override fun observeRecordsForUser(userId: String): Flow<List<PersonalRecordEntity>> = personalRecordDao.observeRecordsForUser(userId)

    override fun observeRecordsForExercise(userId: String, exerciseId: String): Flow<List<PersonalRecordEntity>> =
        personalRecordDao.observeRecordsForExercise(userId, exerciseId)

    override fun observeTopRecordForMetric(userId: String, metricType: String): Flow<PersonalRecordEntity?> =
        personalRecordDao.observeTopRecordForMetric(userId, metricType)

    override fun observeRecordsInRange(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<List<PersonalRecordEntity>> = personalRecordDao.observeRecordsInRange(userId, startEpochMillis, endEpochMillis)

    override suspend fun upsertRecord(record: PersonalRecordEntity) = personalRecordDao.upsert(record)

    override suspend fun upsertRecords(records: List<PersonalRecordEntity>) = personalRecordDao.upsertAll(records)

    override suspend fun deleteRecord(record: PersonalRecordEntity) = personalRecordDao.delete(record)
}
