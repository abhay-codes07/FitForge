package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity
import kotlinx.coroutines.flow.Flow

interface WorkoutExerciseRepository {
    fun observeWorkoutExercise(workoutExerciseId: String): Flow<WorkoutExerciseEntity?>
    fun observeExercisesForWorkout(workoutId: String): Flow<List<WorkoutExerciseEntity>>
    suspend fun getExercisesForWorkout(workoutId: String): List<WorkoutExerciseEntity>
    suspend fun upsertWorkoutExercise(workoutExercise: WorkoutExerciseEntity)
    suspend fun upsertWorkoutExercises(workoutExercises: List<WorkoutExerciseEntity>)
    suspend fun deleteExercisesByWorkoutId(workoutId: String)
    suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExerciseEntity)
}
