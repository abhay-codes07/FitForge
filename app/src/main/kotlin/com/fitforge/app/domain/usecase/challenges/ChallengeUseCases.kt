package com.fitforge.app.domain.usecase.challenges

import com.fitforge.app.data.local.db.entity.ChallengeEntity
import com.fitforge.app.domain.repository.ChallengeRepository
import com.fitforge.app.domain.repository.UserRepository
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf

class GetActiveChallengesUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
) {
    operator fun invoke(): Flow<List<ChallengeEntity>> {
        return challengeRepository.observeChallengesByStatus("active")
    }
}

class GetMyChallengesUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<ChallengeEntity>> {
        val user = userRepository.observePrimaryUser()
        return user.firstOrNull()?.let { u ->
            challengeRepository.observeChallengesForParticipant(u.id)
        } ?: flowOf(emptyList())
    }
}

class GetCreatedChallengesUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<ChallengeEntity>> {
        val user = userRepository.observePrimaryUser()
        return user.firstOrNull()?.let { u ->
            challengeRepository.observeCreatedChallenges(u.id)
        } ?: flowOf(emptyList())
    }
}

class CreateChallengeUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        challengeType: String,
        goalValue: Double,
        unit: String,
        durationDays: Int,
    ): Result<String> {
        return runCatching {
            val user = userRepository.getPrimaryUser()
                ?: error("User not logged in")

            val now = System.currentTimeMillis()
            val challengeId = UUID.randomUUID().toString()

            val challenge = ChallengeEntity(
                id = challengeId,
                creatorUserId = user.id,
                title = title,
                description = description,
                challengeType = challengeType,
                goalValue = goalValue,
                unit = unit,
                participantIds = setOf(user.id),
                winnerUserId = null,
                status = "active",
                startAtEpochMillis = now,
                endAtEpochMillis = now + (durationDays * 24 * 60 * 60 * 1000L),
                createdAtEpochMillis = now,
            )

            challengeRepository.upsertChallenge(challenge)
            challengeId
        }
    }
}

class JoinChallengeUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(challengeId: String): Result<Unit> {
        return runCatching {
            val user = userRepository.getPrimaryUser()
                ?: error("User not logged in")

            val challenge = challengeRepository.observeChallenge(challengeId).firstOrNull()
                ?: error("Challenge not found")

            if (challenge.status != "active") {
                error("Challenge is not active")
            }

            if (user.id in challenge.participantIds) {
                return@runCatching // Already joined
            }

            val updated = challenge.copy(
                participantIds = challenge.participantIds + user.id,
            )

            challengeRepository.upsertChallenge(updated)
        }
    }
}

class LeaveChallengeUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(challengeId: String): Result<Unit> {
        return runCatching {
            val user = userRepository.getPrimaryUser()
                ?: error("User not logged in")

            val challenge = challengeRepository.observeChallenge(challengeId).firstOrNull()
                ?: error("Challenge not found")

            if (user.id == challenge.creatorUserId) {
                error("Creator cannot leave challenge")
            }

            val updated = challenge.copy(
                participantIds = challenge.participantIds - user.id,
            )

            challengeRepository.upsertChallenge(updated)
        }
    }
}

class CompleteChallengeUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
) {
    suspend operator fun invoke(challengeId: String, winnerUserId: String?): Result<Unit> {
        return runCatching {
            val challenge = challengeRepository.observeChallenge(challengeId).firstOrNull()
                ?: error("Challenge not found")

            val updated = challenge.copy(
                status = "completed",
                winnerUserId = winnerUserId,
            )

            challengeRepository.upsertChallenge(updated)
        }
    }
}

class DeleteChallengeUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(challengeId: String): Result<Unit> {
        return runCatching {
            val user = userRepository.getPrimaryUser()
                ?: error("User not logged in")

            val challenge = challengeRepository.observeChallenge(challengeId).firstOrNull()
                ?: error("Challenge not found")

            if (user.id != challenge.creatorUserId) {
                error("Only creator can delete challenge")
            }

            challengeRepository.deleteChallenge(challenge)
        }
    }
}
