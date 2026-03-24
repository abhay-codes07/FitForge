package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementDao {
    @Query("SELECT * FROM body_measurements WHERE userId = :userId ORDER BY recordedAtEpochMillis DESC")
    fun observeMeasurementsForUser(userId: String): Flow<List<BodyMeasurementEntity>>

    @Query("SELECT * FROM body_measurements WHERE userId = :userId ORDER BY recordedAtEpochMillis DESC LIMIT 1")
    fun observeLatestMeasurement(userId: String): Flow<BodyMeasurementEntity?>

    @Query("SELECT * FROM body_measurements WHERE userId = :userId ORDER BY recordedAtEpochMillis DESC LIMIT 1")
    suspend fun getLatestMeasurement(userId: String): BodyMeasurementEntity?

    @Query(
        """
        SELECT * FROM body_measurements
        WHERE userId = :userId
          AND recordedAtEpochMillis BETWEEN :startEpochMillis AND :endEpochMillis
        ORDER BY recordedAtEpochMillis ASC
        """,
    )
    fun observeMeasurementsInRange(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<List<BodyMeasurementEntity>>

    @Upsert
    suspend fun upsert(measurement: BodyMeasurementEntity)

    @Upsert
    suspend fun upsertAll(measurements: List<BodyMeasurementEntity>)

    @Delete
    suspend fun delete(measurement: BodyMeasurementEntity)
}
