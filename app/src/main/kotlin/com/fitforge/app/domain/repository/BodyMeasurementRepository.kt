package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import kotlinx.coroutines.flow.Flow

interface BodyMeasurementRepository {
    fun observeMeasurementsForUser(userId: String): Flow<List<BodyMeasurementEntity>>
    fun observeLatestMeasurement(userId: String): Flow<BodyMeasurementEntity?>
    suspend fun getLatestMeasurement(userId: String): BodyMeasurementEntity?
    fun observeMeasurementsInRange(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<List<BodyMeasurementEntity>>
    suspend fun upsertMeasurement(measurement: BodyMeasurementEntity)
    suspend fun upsertMeasurements(measurements: List<BodyMeasurementEntity>)
    suspend fun deleteMeasurement(measurement: BodyMeasurementEntity)
}
