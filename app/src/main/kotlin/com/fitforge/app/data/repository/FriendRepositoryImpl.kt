package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.FriendDao
import com.fitforge.app.data.local.db.entity.FriendEntity
import com.fitforge.app.domain.repository.FriendRepository
import com.fitforge.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val friendDao: FriendDao,
    private val userRepository: UserRepository,
) : FriendRepository {

    override suspend fun sendFriendRequest(
        userId: String,
        friendEmail: String,
    ): Result<FriendEntity> = runCatching {
        // In a real app, this would query a user API to find the friend by email
        // For now, we'll create a mock friend request
        val friendshipId = UUID.randomUUID().toString()
        val nowMillis = System.currentTimeMillis()

        val friendship = FriendEntity(
            id = friendshipId,
            userId = userId,
            friendUserId = "friend_${UUID.randomUUID()}",
            friendName = friendEmail.substringBefore("@"),
            friendEmail = friendEmail,
            friendPhotoUrl = null,
            status = "pending",
            initiatedByMe = true,
            createdAtEpochMillis = nowMillis,
            updatedAtEpochMillis = nowMillis,
        )

        friendDao.insertFriend(friendship)
        friendship
    }

    override suspend fun acceptFriendRequest(friendshipId: String): Result<Unit> = runCatching {
        val friendship = friendDao.getFriendById(friendshipId)
            ?: throw IllegalArgumentException("Friend request not found")

        val updated = friendship.copy(
            status = "accepted",
            updatedAtEpochMillis = System.currentTimeMillis(),
        )
        friendDao.updateFriend(updated)
    }

    override suspend fun declineFriendRequest(friendshipId: String): Result<Unit> = runCatching {
        friendDao.deleteFriend(friendshipId)
    }

    override suspend fun removeFriend(friendshipId: String): Result<Unit> = runCatching {
        friendDao.deleteFriend(friendshipId)
    }

    override fun getFriends(userId: String): Flow<List<FriendEntity>> {
        return friendDao.getFriendsForUser(userId)
    }

    override fun getPendingRequests(userId: String): Flow<List<FriendEntity>> {
        return friendDao.getPendingFriendRequests(userId)
    }

    override fun getFriendCount(userId: String): Flow<Int> {
        return friendDao.getFriendCount(userId)
    }

    override fun getPendingRequestCount(userId: String): Flow<Int> {
        return friendDao.getPendingRequestCount(userId)
    }

    override suspend fun getFriendship(userId: String, friendUserId: String): FriendEntity? {
        return friendDao.getFriendship(userId, friendUserId)
    }
}
