package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutExerciseDao {
    @Query("SELECT * FROM workout_exercises WHERE id = :workoutExerciseId LIMIT 1")
    fun observeWorkoutExercise(workoutExerciseId: String): Flow<WorkoutExerciseEntity?>

    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY sequenceIndex ASC")
    fun observeExercisesForWorkout(workoutId: String): Flow<List<WorkoutExerciseEntity>>

    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY sequenceIndex ASC")
    suspend fun getExercisesForWorkout(workoutId: String): List<WorkoutExerciseEntity>

    @Upsert
    suspend fun upsert(workoutExercise: WorkoutExerciseEntity)

    @Upsert
    suspend fun upsertAll(workoutExercises: List<WorkoutExerciseEntity>)

    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteByWorkoutId(workoutId: String)

    @Delete
    suspend fun delete(workoutExercise: WorkoutExerciseEntity)
}
