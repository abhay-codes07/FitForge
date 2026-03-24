package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.BodyMeasurementDao
import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import com.fitforge.app.domain.repository.BodyMeasurementRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class BodyMeasurementRepositoryImpl @Inject constructor(
    private val bodyMeasurementDao: BodyMeasurementDao,
) : BodyMeasurementRepository {
    override fun observeMeasurementsForUser(userId: String): Flow<List<BodyMeasurementEntity>> =
        bodyMeasurementDao.observeMeasurementsForUser(userId)

    override fun observeLatestMeasurement(userId: String): Flow<BodyMeasurementEntity?> =
        bodyMeasurementDao.observeLatestMeasurement(userId)

    override suspend fun getLatestMeasurement(userId: String): BodyMeasurementEntity? =
        bodyMeasurementDao.getLatestMeasurement(userId)

    override fun observeMeasurementsInRange(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<List<BodyMeasurementEntity>> = bodyMeasurementDao.observeMeasurementsInRange(userId, startEpochMillis, endEpochMillis)

    override suspend fun upsertMeasurement(measurement: BodyMeasurementEntity) = bodyMeasurementDao.upsert(measurement)

    override suspend fun upsertMeasurements(measurements: List<BodyMeasurementEntity>) = bodyMeasurementDao.upsertAll(measurements)

    override suspend fun deleteMeasurement(measurement: BodyMeasurementEntity) = bodyMeasurementDao.delete(measurement)
}
