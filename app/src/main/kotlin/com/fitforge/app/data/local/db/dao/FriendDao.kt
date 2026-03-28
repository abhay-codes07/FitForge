package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitforge.app.data.local.db.entity.FriendEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendEntity>)

    @Update
    suspend fun updateFriend(friend: FriendEntity)

    @Query("SELECT * FROM friends WHERE userId = :userId AND status = 'accepted' ORDER BY friendName ASC")
    fun getFriendsForUser(userId: String): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE userId = :userId AND status = 'pending' AND initiatedByMe = 0 ORDER BY createdAtEpochMillis DESC")
    fun getPendingFriendRequests(userId: String): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE id = :friendId")
    suspend fun getFriendById(friendId: String): FriendEntity?

    @Query("SELECT * FROM friends WHERE userId = :userId AND friendUserId = :friendUserId")
    suspend fun getFriendship(userId: String, friendUserId: String): FriendEntity?

    @Query("DELETE FROM friends WHERE id = :friendId")
    suspend fun deleteFriend(friendId: String)

    @Query("SELECT COUNT(*) FROM friends WHERE userId = :userId AND status = 'accepted'")
    fun getFriendCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM friends WHERE userId = :userId AND status = 'pending' AND initiatedByMe = 0")
    fun getPendingRequestCount(userId: String): Flow<Int>
}
