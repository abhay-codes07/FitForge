package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitforge.app.data.local.db.entity.ActivityCommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityCommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: ActivityCommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<ActivityCommentEntity>)

    @Query("SELECT * FROM activity_comments WHERE activityId = :activityId ORDER BY createdAtEpochMillis ASC")
    fun getCommentsForActivity(activityId: String): Flow<List<ActivityCommentEntity>>

    @Query("DELETE FROM activity_comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: String)

    @Query("SELECT COUNT(*) FROM activity_comments WHERE activityId = :activityId")
    suspend fun getCommentCount(activityId: String): Int
}
