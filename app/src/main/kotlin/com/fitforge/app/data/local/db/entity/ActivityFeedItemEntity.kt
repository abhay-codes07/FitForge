package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_feed")
data class ActivityFeedItemEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val userName: String,
    val userPhotoUrl: String? = null,
    val activityType: String, // workout_completed, achievement_unlocked, challenge_joined, pr_broken, streak_milestone
    val activityTitle: String,
    val activityDescription: String,
    val workoutId: String? = null,
    val achievementId: String? = null,
    val challengeId: String? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val createdAtEpochMillis: Long,
)
