package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fitforge.app.data.local.db.entity.ChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges WHERE id = :challengeId LIMIT 1")
    fun observeChallenge(challengeId: String): Flow<ChallengeEntity?>

    @Query("SELECT * FROM challenges WHERE creatorUserId = :userId ORDER BY createdAtEpochMillis DESC")
    fun observeCreatedChallenges(userId: String): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE status = :status ORDER BY startAtEpochMillis DESC")
    fun observeChallengesByStatus(status: String): Flow<List<ChallengeEntity>>

    @Query(
        """
        SELECT * FROM challenges
        WHERE participantIds LIKE '%' || :userId || '%'
        ORDER BY startAtEpochMillis DESC
        """,
    )
    fun observeChallengesForParticipant(userId: String): Flow<List<ChallengeEntity>>

    @Upsert
    suspend fun upsert(challenge: ChallengeEntity)

    @Upsert
    suspend fun upsertAll(challenges: List<ChallengeEntity>)

    @Delete
    suspend fun delete(challenge: ChallengeEntity)
}
