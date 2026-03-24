package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "programs",
    indices = [
        Index(value = ["goal"]),
        Index(value = ["difficulty"]),
        Index(value = ["isPremium"]),
    ],
)
data class ProgramEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val goal: String,
    val difficulty: String,
    val durationWeeks: Int,
    val workoutIds: List<String>,
    val coverImageUrl: String?,
    val isPremium: Boolean,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
