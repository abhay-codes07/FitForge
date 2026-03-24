package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.WorkoutDao
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.domain.repository.WorkoutRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
) : WorkoutRepository {
    override fun observeWorkout(workoutId: String): Flow<WorkoutEntity?> = workoutDao.observeWorkout(workoutId)

    override suspend fun getWorkout(workoutId: String): WorkoutEntity? = workoutDao.getWorkout(workoutId)

    override fun observeWorkoutsForUser(userId: String): Flow<List<WorkoutEntity>> = workoutDao.observeWorkoutsForUser(userId)

    override fun observeScheduledWorkoutsForRange(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<List<WorkoutEntity>> = workoutDao.observeScheduledWorkoutsForRange(userId, startEpochMillis, endEpochMillis)

    override fun observeNextScheduledWorkout(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<WorkoutEntity?> = workoutDao.observeNextScheduledWorkout(userId, startEpochMillis, endEpochMillis)

    override fun observeWorkoutsByStatus(userId: String, status: String): Flow<List<WorkoutEntity>> = workoutDao.observeWorkoutsByStatus(userId, status)

    override fun observeRecentCompletedWorkouts(userId: String, limit: Int): Flow<List<WorkoutEntity>> = workoutDao.observeRecentCompletedWorkouts(userId, limit)

    override suspend fun upsertWorkout(workout: WorkoutEntity) = workoutDao.upsert(workout)

    override suspend fun upsertWorkouts(workouts: List<WorkoutEntity>) = workoutDao.upsertAll(workouts)

    override suspend fun deleteWorkout(workout: WorkoutEntity) = workoutDao.delete(workout)
}
