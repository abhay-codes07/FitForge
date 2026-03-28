package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.ExerciseSetEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity

interface CloudWorkoutSyncRepository {
    suspend fun pushWorkout(workout: WorkoutEntity): Result<Unit>
    suspend fun pushWorkoutExercises(workoutExercises: List<WorkoutExerciseEntity>): Result<Unit>
    suspend fun pushExerciseSets(exerciseSets: List<ExerciseSetEntity>): Result<Unit>
    suspend fun pullWorkoutsForUser(userId: String): Result<List<WorkoutEntity>>
    suspend fun pullWorkoutExercisesForWorkout(workoutId: String): Result<List<WorkoutExerciseEntity>>
    suspend fun pullExerciseSetsForWorkout(workoutId: String): Result<List<ExerciseSetEntity>>
}
