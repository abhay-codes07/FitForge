package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "personal_records",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["exerciseId"]),
        Index(value = ["workoutId"]),
        Index(value = ["metricType"]),
        Index(value = ["achievedAtEpochMillis"]),
    ],
)
data class PersonalRecordEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val exerciseId: String?,
    val workoutId: String?,
    val metricType: String,
    val value: Double,
    val unit: String,
    val achievedAtEpochMillis: Long,
    val notes: String?,
)
