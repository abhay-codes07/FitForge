package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_shares")
data class WorkoutShareEntity(
    @PrimaryKey
    val id: String,
    val workoutId: String,
    val sharedByUserId: String,
    val sharedWithUserIds: List<String>, // Can be converted to JSON string in DAO
    val shareMessage: String? = null,
    val sharedAtEpochMillis: Long,
)
