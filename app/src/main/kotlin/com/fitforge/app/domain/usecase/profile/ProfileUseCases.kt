package com.fitforge.app.domain.usecase.profile

import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.domain.repository.ProgramRepository
import com.fitforge.app.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(userId: String): Flow<UserEntity?> = userRepository.observeUser(userId)
}

class ObservePrimaryUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<UserEntity?> = userRepository.observePrimaryUser()
}

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(user: UserEntity) {
        require(user.id.isNotBlank()) { "User id must not be blank." }
        require(user.displayName.isNotBlank()) { "Display name must not be blank." }
        userRepository.upsertUser(user)
    }
}

class ObserveProgramsByGoalUseCase @Inject constructor(
    private val programRepository: ProgramRepository,
) {
    operator fun invoke(goal: String) = programRepository.observeProgramsByGoal(goal)
}

class ObserveProgramsByPremiumStateUseCase @Inject constructor(
    private val programRepository: ProgramRepository,
) {
    operator fun invoke(isPremium: Boolean) = programRepository.observeProgramsByPremiumState(isPremium)
}
