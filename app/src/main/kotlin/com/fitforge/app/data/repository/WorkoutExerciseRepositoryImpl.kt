package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.WorkoutExerciseDao
import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity
import com.fitforge.app.domain.repository.WorkoutExerciseRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class WorkoutExerciseRepositoryImpl @Inject constructor(
    private val workoutExerciseDao: WorkoutExerciseDao,
) : WorkoutExerciseRepository {
    override fun observeWorkoutExercise(workoutExerciseId: String): Flow<WorkoutExerciseEntity?> =
        workoutExerciseDao.observeWorkoutExercise(workoutExerciseId)

    override fun observeExercisesForWorkout(workoutId: String): Flow<List<WorkoutExerciseEntity>> =
        workoutExerciseDao.observeExercisesForWorkout(workoutId)

    override suspend fun getExercisesForWorkout(workoutId: String): List<WorkoutExerciseEntity> =
        workoutExerciseDao.getExercisesForWorkout(workoutId)

    override suspend fun upsertWorkoutExercise(workoutExercise: WorkoutExerciseEntity) =
        workoutExerciseDao.upsert(workoutExercise)

    override suspend fun upsertWorkoutExercises(workoutExercises: List<WorkoutExerciseEntity>) =
        workoutExerciseDao.upsertAll(workoutExercises)

    override suspend fun deleteExercisesByWorkoutId(workoutId: String) = workoutExerciseDao.deleteByWorkoutId(workoutId)

    override suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExerciseEntity) =
        workoutExerciseDao.delete(workoutExercise)
}
