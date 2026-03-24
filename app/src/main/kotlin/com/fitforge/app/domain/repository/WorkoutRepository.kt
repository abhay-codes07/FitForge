package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun observeWorkout(workoutId: String): Flow<WorkoutEntity?>
    suspend fun getWorkout(workoutId: String): WorkoutEntity?
    fun observeWorkoutsForUser(userId: String): Flow<List<WorkoutEntity>>
    fun observeScheduledWorkoutsForRange(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<List<WorkoutEntity>>
    fun observeNextScheduledWorkout(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<WorkoutEntity?>
    fun observeWorkoutsByStatus(userId: String, status: String): Flow<List<WorkoutEntity>>
    fun observeRecentCompletedWorkouts(userId: String, limit: Int): Flow<List<WorkoutEntity>>
    suspend fun upsertWorkout(workout: WorkoutEntity)
    suspend fun upsertWorkouts(workouts: List<WorkoutEntity>)
    suspend fun deleteWorkout(workout: WorkoutEntity)
}
