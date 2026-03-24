package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.ChallengeEntity
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    fun observeChallenge(challengeId: String): Flow<ChallengeEntity?>
    fun observeCreatedChallenges(userId: String): Flow<List<ChallengeEntity>>
    fun observeChallengesByStatus(status: String): Flow<List<ChallengeEntity>>
    fun observeChallengesForParticipant(userId: String): Flow<List<ChallengeEntity>>
    suspend fun upsertChallenge(challenge: ChallengeEntity)
    suspend fun upsertChallenges(challenges: List<ChallengeEntity>)
    suspend fun deleteChallenge(challenge: ChallengeEntity)
}
