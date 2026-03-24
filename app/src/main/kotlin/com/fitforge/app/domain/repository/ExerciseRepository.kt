package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun observeExercises(): Flow<List<ExerciseEntity>>
    fun observeExercise(exerciseId: String): Flow<ExerciseEntity?>
    suspend fun getExercise(exerciseId: String): ExerciseEntity?
    fun searchExercises(query: String): Flow<List<ExerciseEntity>>
    fun observeExercisesByCategory(category: String): Flow<List<ExerciseEntity>>
    fun observeExercisesByDifficulty(difficulty: String): Flow<List<ExerciseEntity>>
    fun observeExercisesByPremiumState(isPremium: Boolean): Flow<List<ExerciseEntity>>
    suspend fun upsertExercise(exercise: ExerciseEntity)
    suspend fun upsertExercises(exercises: List<ExerciseEntity>)
    suspend fun deleteExercise(exercise: ExerciseEntity)
}
