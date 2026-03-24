package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "challenges",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["creatorUserId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["winnerUserId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index(value = ["creatorUserId"]),
        Index(value = ["winnerUserId"]),
        Index(value = ["status"]),
        Index(value = ["startAtEpochMillis"]),
    ],
)
data class ChallengeEntity(
    @PrimaryKey val id: String,
    val creatorUserId: String,
    val title: String,
    val description: String,
    val challengeType: String,
    val goalValue: Double,
    val unit: String,
    val participantIds: Set<String>,
    val winnerUserId: String?,
    val status: String,
    val startAtEpochMillis: Long,
    val endAtEpochMillis: Long,
    val createdAtEpochMillis: Long,
)
