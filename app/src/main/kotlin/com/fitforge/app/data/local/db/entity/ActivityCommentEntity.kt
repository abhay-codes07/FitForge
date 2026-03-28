package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_comments")
data class ActivityCommentEntity(
    @PrimaryKey
    val id: String,
    val activityId: String,
    val userId: String,
    val userName: String,
    val userPhotoUrl: String? = null,
    val commentText: String,
    val createdAtEpochMillis: Long,
)
