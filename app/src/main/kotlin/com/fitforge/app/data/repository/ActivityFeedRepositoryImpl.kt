package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.ActivityCommentDao
import com.fitforge.app.data.local.db.dao.ActivityFeedDao
import com.fitforge.app.data.local.db.entity.ActivityCommentEntity
import com.fitforge.app.data.local.db.entity.ActivityFeedItemEntity
import com.fitforge.app.domain.repository.ActivityFeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ActivityFeedRepositoryImpl @Inject constructor(
    private val activityFeedDao: ActivityFeedDao,
    private val activityCommentDao: ActivityCommentDao,
) : ActivityFeedRepository {

    override suspend fun createActivity(activity: ActivityFeedItemEntity): Result<Unit> = runCatching {
        activityFeedDao.insertActivity(activity)
    }

    override fun getRecentActivities(limit: Int): Flow<List<ActivityFeedItemEntity>> {
        return activityFeedDao.getRecentActivities(limit)
    }

    override fun getActivitiesForUser(userId: String, limit: Int): Flow<List<ActivityFeedItemEntity>> {
        return activityFeedDao.getActivitiesForUser(userId, limit)
    }

    override suspend fun likeActivity(activityId: String): Result<Unit> = runCatching {
        val activity = activityFeedDao.getActivityById(activityId)
            ?: throw IllegalArgumentException("Activity not found")

        val updated = activity.copy(
            likeCount = activity.likeCount + 1,
            isLikedByMe = true,
        )
        activityFeedDao.updateActivity(updated)
    }

    override suspend fun unlikeActivity(activityId: String): Result<Unit> = runCatching {
        val activity = activityFeedDao.getActivityById(activityId)
            ?: throw IllegalArgumentException("Activity not found")

        val updated = activity.copy(
            likeCount = maxOf(0, activity.likeCount - 1),
            isLikedByMe = false,
        )
        activityFeedDao.updateActivity(updated)
    }

    override suspend fun addComment(comment: ActivityCommentEntity): Result<Unit> = runCatching {
        activityCommentDao.insertComment(comment)

        // Update comment count in activity
        val activity = activityFeedDao.getActivityById(comment.activityId)
        if (activity != null) {
            val commentCount = activityCommentDao.getCommentCount(comment.activityId)
            val updated = activity.copy(commentCount = commentCount)
            activityFeedDao.updateActivity(updated)
        }
    }

    override fun getCommentsForActivity(activityId: String): Flow<List<ActivityCommentEntity>> {
        return activityCommentDao.getCommentsForActivity(activityId)
    }

    override suspend fun deleteActivity(activityId: String): Result<Unit> = runCatching {
        activityFeedDao.deleteActivity(activityId)
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> = runCatching {
        activityCommentDao.deleteComment(commentId)
    }
}
