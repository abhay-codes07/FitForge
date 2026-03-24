package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_logs",
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
        Index(value = ["userId", "logDateEpochDay"], unique = true),
    ],
)
data class DailyLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val logDateEpochDay: Long,
    val steps: Int,
    val activeCalories: Int,
    val workoutMinutes: Int,
    val waterIntakeMl: Int,
    val sleepMinutes: Int?,
    val distanceMeters: Float,
    val restingHeartRate: Int?,
    val averageHeartRate: Int?,
    val readinessScore: Int?,
    val notes: String?,
)
