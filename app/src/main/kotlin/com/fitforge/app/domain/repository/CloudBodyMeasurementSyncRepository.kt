package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity

interface CloudBodyMeasurementSyncRepository {
    suspend fun pushBodyMeasurement(measurement: BodyMeasurementEntity): Result<Unit>
    suspend fun pullBodyMeasurementsForUser(userId: String): Result<List<BodyMeasurementEntity>>
}
