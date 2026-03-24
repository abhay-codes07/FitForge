package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workouts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["scheduledDateEpochMillis"]),
        Index(value = ["status"]),
    ],
)
data class WorkoutEntity(
    @PrimaryKey val id: String,
    val userId: String?,
    val name: String,
    val description: String,
    val workoutType: String,
    val difficulty: String,
    val estimatedDurationMinutes: Int,
    val estimatedCalories: Int?,
    val source: String,
    val scheduledDateEpochMillis: Long?,
    val completedAtEpochMillis: Long?,
    val status: String,
    val notes: String?,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
