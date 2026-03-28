package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_templates")
data class WorkoutTemplateEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val description: String? = null,
    val workoutType: String,
    val difficulty: String,
    val estimatedDurationMinutes: Int,
    val estimatedCalories: Int? = null,
    val isPublic: Boolean = false,
    val useCount: Int = 0,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
