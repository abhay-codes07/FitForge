package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    indices = [
        Index(value = ["category"]),
        Index(value = ["difficulty"]),
        Index(value = ["isPremium"]),
    ],
)
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val equipment: Set<String>,
    val primaryMuscles: Set<String>,
    val secondaryMuscles: Set<String>,
    val instructions: List<String>,
    val estimatedDurationSeconds: Int?,
    val estimatedCalories: Int?,
    val imageUrl: String?,
    val videoUrl: String?,
    val isPremium: Boolean,
    val source: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
