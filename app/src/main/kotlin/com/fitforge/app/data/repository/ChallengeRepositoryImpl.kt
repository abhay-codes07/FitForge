package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.ChallengeDao
import com.fitforge.app.data.local.db.entity.ChallengeEntity
import com.fitforge.app.domain.repository.ChallengeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ChallengeRepositoryImpl @Inject constructor(
    private val challengeDao: ChallengeDao,
) : ChallengeRepository {
    override fun observeChallenge(challengeId: String): Flow<ChallengeEntity?> = challengeDao.observeChallenge(challengeId)

    override fun observeCreatedChallenges(userId: String): Flow<List<ChallengeEntity>> = challengeDao.observeCreatedChallenges(userId)

    override fun observeChallengesByStatus(status: String): Flow<List<ChallengeEntity>> = challengeDao.observeChallengesByStatus(status)

    override fun observeChallengesForParticipant(userId: String): Flow<List<ChallengeEntity>> = challengeDao.observeChallengesForParticipant(userId)

    override suspend fun upsertChallenge(challenge: ChallengeEntity) = challengeDao.upsert(challenge)

    override suspend fun upsertChallenges(challenges: List<ChallengeEntity>) = challengeDao.upsertAll(challenges)

    override suspend fun deleteChallenge(challenge: ChallengeEntity) = challengeDao.delete(challenge)
}
