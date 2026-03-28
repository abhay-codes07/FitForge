package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val friendUserId: String,
    val friendName: String,
    val friendEmail: String,
    val friendPhotoUrl: String? = null,
    val status: String, // pending, accepted, blocked
    val initiatedByMe: Boolean,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
