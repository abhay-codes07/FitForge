package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_notes")
data class WorkoutNoteEntity(
    @PrimaryKey
    val workoutId: String,
    val userId: String,
    val rating: Int, // 1-5 stars
    val difficulty: Int, // 1-5 perceived difficulty
    val energyLevel: Int, // 1-5 energy before workout
    val note: String,
    val createdAtEpochMillis: Long,
)
