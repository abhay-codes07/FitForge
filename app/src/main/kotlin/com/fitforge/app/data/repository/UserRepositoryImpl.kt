package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.UserDao
import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
) : UserRepository {
    override fun observeUser(userId: String): Flow<UserEntity?> = userDao.observeUser(userId)

    override suspend fun getUser(userId: String): UserEntity? = userDao.getUser(userId)

    override fun observePrimaryUser(): Flow<UserEntity?> = userDao.observePrimaryUser()

    override suspend fun getPrimaryUser(): UserEntity? = userDao.getPrimaryUser()

    override suspend fun upsertUser(user: UserEntity) = userDao.upsert(user)

    override suspend fun upsertUsers(users: List<UserEntity>) = userDao.upsertAll(users)

    override suspend fun deleteUser(user: UserEntity) = userDao.delete(user)
}
