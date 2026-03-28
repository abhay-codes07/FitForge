package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.UserEntity

interface CloudUserSyncRepository {
    suspend fun pushUser(user: UserEntity): Result<Unit>
    suspend fun pullUser(userId: String): Result<UserEntity?>
}
