package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "template_exercises")
data class TemplateExerciseEntity(
    @PrimaryKey
    val id: String,
    val templateId: String,
    val exerciseId: String,
    val exerciseName: String,
    val orderIndex: Int,
    val targetSets: Int,
    val targetReps: Int,
    val targetWeight: Float? = null,
    val restSeconds: Int = 60,
    val notes: String? = null,
)
