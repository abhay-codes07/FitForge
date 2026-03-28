package com.fitforge.app.domain.usecase.social

import com.fitforge.app.data.local.db.entity.ActivityCommentEntity
import com.fitforge.app.data.local.db.entity.ActivityFeedItemEntity
import com.fitforge.app.domain.repository.ActivityFeedRepository
import com.fitforge.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

class GetActivityFeedUseCase @Inject constructor(
    private val activityFeedRepository: ActivityFeedRepository,
) {
    operator fun invoke(limit: Int = 50): Flow<List<ActivityFeedItemEntity>> {
        return activityFeedRepository.getRecentActivities(limit)
    }
}

class GetMyActivitiesUseCase @Inject constructor(
    private val activityFeedRepository: ActivityFeedRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(limit: Int = 20): Flow<List<ActivityFeedItemEntity>> {
        val currentUser = userRepository.getPrimaryUser() ?: return flowOf(emptyList())
        return activityFeedRepository.getActivitiesForUser(currentUser.id, limit)
    }
}

class CreateActivityUseCase @Inject constructor(
    private val activityFeedRepository: ActivityFeedRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        activityType: String,
        title: String,
        description: String,
        workoutId: String? = null,
        achievementId: String? = null,
        challengeId: String? = null,
    ): Result<Unit> {
        val currentUser = userRepository.getPrimaryUser() ?: return Result.failure(
            Exception("User not logged in")
        )

        val activity = ActivityFeedItemEntity(
            id = UUID.randomUUID().toString(),
            userId = currentUser.id,
            userName = currentUser.name,
            userPhotoUrl = currentUser.photoUrl,
            activityType = activityType,
            activityTitle = title,
            activityDescription = description,
            workoutId = workoutId,
            achievementId = achievementId,
            challengeId = challengeId,
            createdAtEpochMillis = System.currentTimeMillis(),
        )

        return activityFeedRepository.createActivity(activity)
    }
}

class LikeActivityUseCase @Inject constructor(
    private val activityFeedRepository: ActivityFeedRepository,
) {
    suspend operator fun invoke(activityId: String): Result<Unit> {
        return activityFeedRepository.likeActivity(activityId)
    }
}

class UnlikeActivityUseCase @Inject constructor(
    private val activityFeedRepository: ActivityFeedRepository,
) {
    suspend operator fun invoke(activityId: String): Result<Unit> {
        return activityFeedRepository.unlikeActivity(activityId)
    }
}

class AddCommentUseCase @Inject constructor(
    private val activityFeedRepository: ActivityFeedRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(activityId: String, commentText: String): Result<Unit> {
        val currentUser = userRepository.getPrimaryUser() ?: return Result.failure(
            Exception("User not logged in")
        )

        val comment = ActivityCommentEntity(
            id = UUID.randomUUID().toString(),
            activityId = activityId,
            userId = currentUser.id,
            userName = currentUser.name,
            userPhotoUrl = currentUser.photoUrl,
            commentText = commentText,
            createdAtEpochMillis = System.currentTimeMillis(),
        )

        return activityFeedRepository.addComment(comment)
    }
}

class GetCommentsForActivityUseCase @Inject constructor(
    private val activityFeedRepository: ActivityFeedRepository,
) {
    operator fun invoke(activityId: String): Flow<List<ActivityCommentEntity>> {
        return activityFeedRepository.getCommentsForActivity(activityId)
    }
}

class DeleteActivityUseCase @Inject constructor(
    private val activityFeedRepository: ActivityFeedRepository,
) {
    suspend operator fun invoke(activityId: String): Result<Unit> {
        return activityFeedRepository.deleteActivity(activityId)
    }
}
