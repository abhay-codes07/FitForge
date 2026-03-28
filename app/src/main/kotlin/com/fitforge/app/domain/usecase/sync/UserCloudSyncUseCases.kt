package com.fitforge.app.domain.usecase.sync

import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.domain.repository.CloudUserSyncRepository
import com.fitforge.app.domain.repository.UserRepository
import javax.inject.Inject

class PushUserToCloudUseCase @Inject constructor(
    private val cloudUserSyncRepository: CloudUserSyncRepository,
) {
    suspend operator fun invoke(user: UserEntity): Result<Unit> {
        return cloudUserSyncRepository.pushUser(user)
    }
}

class PullUserFromCloudUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val cloudUserSyncRepository: CloudUserSyncRepository,
) {
    suspend operator fun invoke(userId: String): Result<Boolean> {
        return cloudUserSyncRepository.pullUser(userId).mapCatching { user ->
            if (user != null) {
                userRepository.upsertUser(user)
                true
            } else {
                false
            }
        }
    }
}
