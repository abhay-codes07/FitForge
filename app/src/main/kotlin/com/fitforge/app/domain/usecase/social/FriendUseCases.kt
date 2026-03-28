package com.fitforge.app.domain.usecase.social

import com.fitforge.app.data.local.db.entity.FriendEntity
import com.fitforge.app.domain.repository.FriendRepository
import com.fitforge.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SendFriendRequestUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(friendEmail: String): Result<FriendEntity> {
        val currentUser = userRepository.getPrimaryUser() ?: return Result.failure(
            Exception("User not logged in")
        )
        return friendRepository.sendFriendRequest(currentUser.id, friendEmail)
    }
}

class AcceptFriendRequestUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
) {
    suspend operator fun invoke(friendshipId: String): Result<Unit> {
        return friendRepository.acceptFriendRequest(friendshipId)
    }
}

class DeclineFriendRequestUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
) {
    suspend operator fun invoke(friendshipId: String): Result<Unit> {
        return friendRepository.declineFriendRequest(friendshipId)
    }
}

class RemoveFriendUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
) {
    suspend operator fun invoke(friendshipId: String): Result<Unit> {
        return friendRepository.removeFriend(friendshipId)
    }
}

class GetFriendsUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<FriendEntity>> {
        val currentUser = userRepository.getPrimaryUser() ?: return flowOf(emptyList())
        return friendRepository.getFriends(currentUser.id)
    }
}

class GetPendingFriendRequestsUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<FriendEntity>> {
        val currentUser = userRepository.getPrimaryUser() ?: return flowOf(emptyList())
        return friendRepository.getPendingRequests(currentUser.id)
    }
}

class GetFriendCountUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<Int> {
        val currentUser = userRepository.getPrimaryUser() ?: return flowOf(0)
        return friendRepository.getFriendCount(currentUser.id)
    }
}

class GetPendingRequestCountUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<Int> {
        val currentUser = userRepository.getPrimaryUser() ?: return flowOf(0)
        return friendRepository.getPendingRequestCount(currentUser.id)
    }
}
