package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.ExerciseSetEntity
import kotlinx.coroutines.flow.Flow

interface ExerciseSetRepository {
    fun observeSetsForWorkout(workoutId: String): Flow<List<ExerciseSetEntity>>
    fun observeSetsForWorkoutExercise(workoutExerciseId: String): Flow<List<ExerciseSetEntity>>
    suspend fun getSetsForWorkout(workoutId: String): List<ExerciseSetEntity>
    suspend fun getSetsForWorkoutExercise(workoutExerciseId: String): List<ExerciseSetEntity>
    suspend fun upsertExerciseSet(exerciseSet: ExerciseSetEntity)
    suspend fun upsertExerciseSets(exerciseSets: List<ExerciseSetEntity>)
    suspend fun deleteSetsByWorkoutExerciseId(workoutExerciseId: String)
    suspend fun deleteExerciseSet(exerciseSet: ExerciseSetEntity)
}
