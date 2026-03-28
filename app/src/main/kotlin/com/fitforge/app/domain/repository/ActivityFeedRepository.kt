package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.ActivityCommentEntity
import com.fitforge.app.data.local.db.entity.ActivityFeedItemEntity
import kotlinx.coroutines.flow.Flow

interface ActivityFeedRepository {
    suspend fun createActivity(activity: ActivityFeedItemEntity): Result<Unit>
    fun getRecentActivities(limit: Int): Flow<List<ActivityFeedItemEntity>>
    fun getActivitiesForUser(userId: String, limit: Int): Flow<List<ActivityFeedItemEntity>>
    suspend fun likeActivity(activityId: String): Result<Unit>
    suspend fun unlikeActivity(activityId: String): Result<Unit>
    suspend fun addComment(comment: ActivityCommentEntity): Result<Unit>
    fun getCommentsForActivity(activityId: String): Flow<List<ActivityCommentEntity>>
    suspend fun deleteActivity(activityId: String): Result<Unit>
    suspend fun deleteComment(commentId: String): Result<Unit>
}
