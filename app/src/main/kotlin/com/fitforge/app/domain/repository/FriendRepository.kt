package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.FriendEntity
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    suspend fun sendFriendRequest(userId: String, friendEmail: String): Result<FriendEntity>
    suspend fun acceptFriendRequest(friendshipId: String): Result<Unit>
    suspend fun declineFriendRequest(friendshipId: String): Result<Unit>
    suspend fun removeFriend(friendshipId: String): Result<Unit>
    fun getFriends(userId: String): Flow<List<FriendEntity>>
    fun getPendingRequests(userId: String): Flow<List<FriendEntity>>
    fun getFriendCount(userId: String): Flow<Int>
    fun getPendingRequestCount(userId: String): Flow<Int>
    suspend fun getFriendship(userId: String, friendUserId: String): FriendEntity?
}
