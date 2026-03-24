package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fitforge.app.data.local.db.entity.ExerciseSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseSetDao {
    @Query("SELECT * FROM exercise_sets WHERE workoutId = :workoutId ORDER BY completedAtEpochMillis ASC, setNumber ASC")
    fun observeSetsForWorkout(workoutId: String): Flow<List<ExerciseSetEntity>>

    @Query("SELECT * FROM exercise_sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber ASC")
    fun observeSetsForWorkoutExercise(workoutExerciseId: String): Flow<List<ExerciseSetEntity>>

    @Query("SELECT * FROM exercise_sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber ASC")
    suspend fun getSetsForWorkoutExercise(workoutExerciseId: String): List<ExerciseSetEntity>

    @Upsert
    suspend fun upsert(exerciseSet: ExerciseSetEntity)

    @Upsert
    suspend fun upsertAll(exerciseSets: List<ExerciseSetEntity>)

    @Query("DELETE FROM exercise_sets WHERE workoutExerciseId = :workoutExerciseId")
    suspend fun deleteByWorkoutExerciseId(workoutExerciseId: String)

    @Delete
    suspend fun delete(exerciseSet: ExerciseSetEntity)
}
