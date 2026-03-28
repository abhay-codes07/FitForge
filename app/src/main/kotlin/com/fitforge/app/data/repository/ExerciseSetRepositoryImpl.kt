package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.ExerciseSetDao
import com.fitforge.app.data.local.db.entity.ExerciseSetEntity
import com.fitforge.app.domain.repository.ExerciseSetRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ExerciseSetRepositoryImpl @Inject constructor(
    private val exerciseSetDao: ExerciseSetDao,
) : ExerciseSetRepository {
    override fun observeSetsForWorkout(workoutId: String): Flow<List<ExerciseSetEntity>> = exerciseSetDao.observeSetsForWorkout(workoutId)

    override fun observeSetsForWorkoutExercise(workoutExerciseId: String): Flow<List<ExerciseSetEntity>> =
        exerciseSetDao.observeSetsForWorkoutExercise(workoutExerciseId)

    override suspend fun getSetsForWorkout(workoutId: String): List<ExerciseSetEntity> =
        exerciseSetDao.getSetsForWorkout(workoutId)

    override suspend fun getSetsForWorkoutExercise(workoutExerciseId: String): List<ExerciseSetEntity> =
        exerciseSetDao.getSetsForWorkoutExercise(workoutExerciseId)

    override suspend fun upsertExerciseSet(exerciseSet: ExerciseSetEntity) = exerciseSetDao.upsert(exerciseSet)

    override suspend fun upsertExerciseSets(exerciseSets: List<ExerciseSetEntity>) = exerciseSetDao.upsertAll(exerciseSets)

    override suspend fun deleteSetsByWorkoutExerciseId(workoutExerciseId: String) =
        exerciseSetDao.deleteByWorkoutExerciseId(workoutExerciseId)

    override suspend fun deleteExerciseSet(exerciseSet: ExerciseSetEntity) = exerciseSetDao.delete(exerciseSet)
}
