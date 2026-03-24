package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
    ],
)
data class UserEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val email: String?,
    val photoUrl: String?,
    val birthDateEpochMillis: Long?,
    val gender: String,
    val heightCm: Float?,
    val currentWeightKg: Float?,
    val targetWeightKg: Float?,
    val fitnessLevel: String,
    val primaryGoals: Set<String>,
    val workoutLocations: Set<String>,
    val availableEquipment: Set<String>,
    val preferredUnitSystem: String,
    val isNotificationEnabled: Boolean,
    val isHealthConnectLinked: Boolean,
    val isPremium: Boolean,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
