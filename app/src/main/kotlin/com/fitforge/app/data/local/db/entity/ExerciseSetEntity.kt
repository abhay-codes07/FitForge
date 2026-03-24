package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = WorkoutExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutExerciseId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["workoutId"]),
        Index(value = ["workoutExerciseId"]),
        Index(value = ["exerciseId"]),
    ],
)
data class ExerciseSetEntity(
    @PrimaryKey val id: String,
    val workoutId: String,
    val workoutExerciseId: String,
    val exerciseId: String,
    val setNumber: Int,
    val reps: Int?,
    val weightKg: Float?,
    val durationSeconds: Int?,
    val distanceMeters: Float?,
    val restDurationSeconds: Int?,
    val rpe: Float?,
    val isWarmUp: Boolean,
    val completedAtEpochMillis: Long?,
    val notes: String?,
)
