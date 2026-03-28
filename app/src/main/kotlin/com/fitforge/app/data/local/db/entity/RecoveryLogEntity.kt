package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recovery_logs")
data class RecoveryLogEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val logDateEpochDay: Long,
    val overallRecoveryScore: Int, // 1-10
    val sleepQuality: Int, // 1-5
    val sleepHours: Float,
    val stressLevel: Int, // 1-5
    val fatigueLevel: Int, // 1-5
    val restingHeartRate: Int? = null,
    val sorenessAreas: String, // JSON string of body areas
    val notes: String? = null,
    val createdAtEpochMillis: Long,
)
