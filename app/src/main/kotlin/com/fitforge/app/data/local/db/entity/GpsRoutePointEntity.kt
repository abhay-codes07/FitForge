package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "gps_route_points",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["workoutId"]),
        Index(value = ["timestampEpochMillis"]),
    ],
)
data class GpsRoutePointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val workoutId: String,
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double?,
    val accuracyMeters: Float?,
    val speedMetersPerSecond: Float?,
    val heartRate: Int?,
    val timestampEpochMillis: Long,
    val segmentIndex: Int,
)
