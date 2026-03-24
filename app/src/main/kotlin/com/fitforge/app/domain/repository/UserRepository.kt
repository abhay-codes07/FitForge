package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(userId: String): Flow<UserEntity?>
    suspend fun getUser(userId: String): UserEntity?
    fun observePrimaryUser(): Flow<UserEntity?>
    suspend fun getPrimaryUser(): UserEntity?
    suspend fun upsertUser(user: UserEntity)
    suspend fun upsertUsers(users: List<UserEntity>)
    suspend fun deleteUser(user: UserEntity)
}
