package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts WHERE id = :workoutId LIMIT 1")
    fun observeWorkout(workoutId: String): Flow<WorkoutEntity?>

    @Query("SELECT * FROM workouts WHERE id = :workoutId LIMIT 1")
    suspend fun getWorkout(workoutId: String): WorkoutEntity?

    @Query("SELECT * FROM workouts WHERE userId = :userId ORDER BY COALESCE(scheduledDateEpochMillis, createdAtEpochMillis) DESC")
    fun observeWorkoutsForUser(userId: String): Flow<List<WorkoutEntity>>

    @Query(
        """
        SELECT * FROM workouts
        WHERE userId = :userId
          AND scheduledDateEpochMillis BETWEEN :startEpochMillis AND :endEpochMillis
        ORDER BY scheduledDateEpochMillis ASC
        """,
    )
    fun observeScheduledWorkoutsForRange(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<List<WorkoutEntity>>

    @Query(
        """
        SELECT * FROM workouts
        WHERE userId = :userId
          AND scheduledDateEpochMillis BETWEEN :startEpochMillis AND :endEpochMillis
        ORDER BY scheduledDateEpochMillis ASC
        LIMIT 1
        """,
    )
    fun observeNextScheduledWorkout(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<WorkoutEntity?>

    @Query(
        """
        SELECT * FROM workouts
        WHERE userId = :userId
          AND status = :status
        ORDER BY COALESCE(completedAtEpochMillis, updatedAtEpochMillis) DESC
        """,
    )
    fun observeWorkoutsByStatus(userId: String, status: String): Flow<List<WorkoutEntity>>

    @Query(
        """
        SELECT * FROM workouts
        WHERE userId = :userId
          AND completedAtEpochMillis IS NOT NULL
        ORDER BY completedAtEpochMillis DESC
        LIMIT :limit
        """,
    )
    fun observeRecentCompletedWorkouts(userId: String, limit: Int): Flow<List<WorkoutEntity>>

    @Upsert
    suspend fun upsert(workout: WorkoutEntity)

    @Upsert
    suspend fun upsertAll(workouts: List<WorkoutEntity>)

    @Delete
    suspend fun delete(workout: WorkoutEntity)
}
