package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitforge.app.data.local.db.entity.ActivityFeedItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityFeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityFeedItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityFeedItemEntity>)

    @Update
    suspend fun updateActivity(activity: ActivityFeedItemEntity)

    @Query("SELECT * FROM activity_feed ORDER BY createdAtEpochMillis DESC LIMIT :limit")
    fun getRecentActivities(limit: Int = 50): Flow<List<ActivityFeedItemEntity>>

    @Query("SELECT * FROM activity_feed WHERE userId = :userId ORDER BY createdAtEpochMillis DESC LIMIT :limit")
    fun getActivitiesForUser(userId: String, limit: Int = 20): Flow<List<ActivityFeedItemEntity>>

    @Query("SELECT * FROM activity_feed WHERE id = :activityId")
    suspend fun getActivityById(activityId: String): ActivityFeedItemEntity?

    @Query("DELETE FROM activity_feed WHERE id = :activityId")
    suspend fun deleteActivity(activityId: String)

    @Query("DELETE FROM activity_feed WHERE createdAtEpochMillis < :timestampMillis")
    suspend fun deleteOldActivities(timestampMillis: Long)
}
