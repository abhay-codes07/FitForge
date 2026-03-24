package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "body_measurements",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["recordedAtEpochMillis"]),
    ],
)
data class BodyMeasurementEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val recordedAtEpochMillis: Long,
    val weightKg: Float?,
    val bodyFatPercent: Float?,
    val chestCm: Float?,
    val waistCm: Float?,
    val hipsCm: Float?,
    val bicepsCm: Float?,
    val thighCm: Float?,
    val calfCm: Float?,
    val neckCm: Float?,
    val progressPhotoUris: List<String>,
    val notes: String?,
)
