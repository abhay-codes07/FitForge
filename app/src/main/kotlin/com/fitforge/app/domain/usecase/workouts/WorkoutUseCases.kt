package com.fitforge.app.domain.usecase.workouts

import com.fitforge.app.data.local.db.entity.ExerciseEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity
import com.fitforge.app.domain.repository.ExerciseRepository
import com.fitforge.app.domain.repository.WorkoutExerciseRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class SearchExercisesUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
) {
    operator fun invoke(query: String): Flow<List<ExerciseEntity>> =
        exerciseRepository.searchExercises(query.trim())
}

class ObserveExercisesByCategoryUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
) {
    operator fun invoke(category: String): Flow<List<ExerciseEntity>> =
        exerciseRepository.observeExercisesByCategory(category)
}

class ObserveExerciseByDifficultyUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
) {
    operator fun invoke(difficulty: String): Flow<List<ExerciseEntity>> =
        exerciseRepository.observeExercisesByDifficulty(difficulty)
}

class ObserveExerciseDetailUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
) {
    operator fun invoke(exerciseId: String): Flow<ExerciseEntity?> =
        exerciseRepository.observeExercise(exerciseId)
}

data class CreateCustomWorkoutRequest(
    val workout: WorkoutEntity,
    val workoutExercises: List<WorkoutExerciseEntity>,
)

class CreateCustomWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val workoutExerciseRepository: WorkoutExerciseRepository,
) {
    suspend operator fun invoke(request: CreateCustomWorkoutRequest) {
        require(request.workoutExercises.isNotEmpty()) {
            "Custom workout must include at least one exercise."
        }

        val ordered = request.workoutExercises.sortedBy { it.sequenceIndex }
        require(ordered.zipWithNext().all { (current, next) -> next.sequenceIndex > current.sequenceIndex }) {
            "Workout exercise sequence indexes must be unique and strictly increasing."
        }

        workoutRepository.upsertWorkout(request.workout)
        workoutExerciseRepository.deleteExercisesByWorkoutId(request.workout.id)
        workoutExerciseRepository.upsertWorkoutExercises(ordered)
    }
}

class ObserveWorkoutDetailUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    operator fun invoke(workoutId: String): Flow<WorkoutEntity?> =
        workoutRepository.observeWorkout(workoutId)
}

class ObserveWorkoutExercisesUseCase @Inject constructor(
    private val workoutExerciseRepository: WorkoutExerciseRepository,
) {
    operator fun invoke(workoutId: String): Flow<List<WorkoutExerciseEntity>> =
        workoutExerciseRepository.observeExercisesForWorkout(workoutId)
}
