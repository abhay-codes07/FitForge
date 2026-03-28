package com.fitforge.app.domain.usecase.sync

import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.domain.repository.CloudWorkoutSyncRepository
import com.fitforge.app.domain.repository.ExerciseSetRepository
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutExerciseRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class PushWorkoutToCloudUseCase @Inject constructor(
    private val cloudWorkoutSyncRepository: CloudWorkoutSyncRepository,
    private val workoutExerciseRepository: WorkoutExerciseRepository,
    private val exerciseSetRepository: ExerciseSetRepository,
) {
    suspend operator fun invoke(workout: WorkoutEntity): Result<Unit> {
        return runCatching {
            // Push workout metadata
            cloudWorkoutSyncRepository.pushWorkout(workout).getOrThrow()

            // Push workout exercises
            val workoutExercises = workoutExerciseRepository.getExercisesForWorkout(workout.id)
            if (workoutExercises.isNotEmpty()) {
                cloudWorkoutSyncRepository.pushWorkoutExercises(workoutExercises).getOrThrow()
            }

            // Push exercise sets
            val exerciseSets = exerciseSetRepository.getSetsForWorkout(workout.id)
            if (exerciseSets.isNotEmpty()) {
                cloudWorkoutSyncRepository.pushExerciseSets(exerciseSets).getOrThrow()
            }
        }
    }
}

class PullWorkoutsFromCloudUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val workoutExerciseRepository: WorkoutExerciseRepository,
    private val exerciseSetRepository: ExerciseSetRepository,
    private val cloudWorkoutSyncRepository: CloudWorkoutSyncRepository,
) {
    suspend operator fun invoke(): Result<Int> {
        val user = userRepository.getPrimaryUser() ?: return Result.success(0)
        return cloudWorkoutSyncRepository.pullWorkoutsForUser(user.id).mapCatching { workouts ->
            if (workouts.isNotEmpty()) {
                // Upsert workouts
                workoutRepository.upsertWorkouts(workouts)

                // Pull and upsert workout exercises and sets for each workout
                workouts.forEach { workout ->
                    cloudWorkoutSyncRepository.pullWorkoutExercisesForWorkout(workout.id).onSuccess { exercises ->
                        if (exercises.isNotEmpty()) {
                            workoutExerciseRepository.upsertWorkoutExercises(exercises)
                        }
                    }
                    cloudWorkoutSyncRepository.pullExerciseSetsForWorkout(workout.id).onSuccess { sets ->
                        if (sets.isNotEmpty()) {
                            exerciseSetRepository.upsertExerciseSets(sets)
                        }
                    }
                }
            }
            workouts.size
        }
    }
}
