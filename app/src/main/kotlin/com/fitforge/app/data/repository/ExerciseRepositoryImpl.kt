package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.ExerciseDao
import com.fitforge.app.data.local.db.entity.ExerciseEntity
import com.fitforge.app.domain.repository.ExerciseRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao,
) : ExerciseRepository {
    override fun observeExercises(): Flow<List<ExerciseEntity>> = exerciseDao.observeExercises()

    override fun observeExercise(exerciseId: String): Flow<ExerciseEntity?> = exerciseDao.observeExercise(exerciseId)

    override suspend fun getExercise(exerciseId: String): ExerciseEntity? = exerciseDao.getExercise(exerciseId)

    override fun searchExercises(query: String): Flow<List<ExerciseEntity>> = exerciseDao.searchExercises(query)

    override fun observeExercisesByCategory(category: String): Flow<List<ExerciseEntity>> = exerciseDao.observeExercisesByCategory(category)

    override fun observeExercisesByDifficulty(difficulty: String): Flow<List<ExerciseEntity>> = exerciseDao.observeExercisesByDifficulty(difficulty)

    override fun observeExercisesByPremiumState(isPremium: Boolean): Flow<List<ExerciseEntity>> = exerciseDao.observeExercisesByPremiumState(isPremium)

    override suspend fun upsertExercise(exercise: ExerciseEntity) = exerciseDao.upsert(exercise)

    override suspend fun upsertExercises(exercises: List<ExerciseEntity>) = exerciseDao.upsertAll(exercises)

    override suspend fun deleteExercise(exercise: ExerciseEntity) = exerciseDao.delete(exercise)
}
