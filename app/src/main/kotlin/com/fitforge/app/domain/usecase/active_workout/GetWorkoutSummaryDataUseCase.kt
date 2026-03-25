package com.fitforge.app.domain.usecase.active_workout

import com.fitforge.app.domain.repository.ExerciseSetRepository
import com.fitforge.app.domain.repository.PersonalRecordRepository
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

data class WorkoutSummaryData(
    val workoutId: String,
    val workoutName: String,
    val completedAtEpochMillis: Long,
    val estimatedDurationMinutes: Int,
    val estimatedCalories: Int,
    val totalSets: Int,
    val totalReps: Int,
    val totalVolumeKg: Float,
    val personalRecordCount: Int,
)

class GetWorkoutSummaryDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val exerciseSetRepository: ExerciseSetRepository,
    private val personalRecordRepository: PersonalRecordRepository,
) {
    suspend operator fun invoke(nowEpochMillis: Long = System.currentTimeMillis()): WorkoutSummaryData {
        val user = userRepository.getPrimaryUser() ?: error("No primary user found")
        val completedWorkout = workoutRepository.observeRecentCompletedWorkouts(user.id, limit = 1)
            .first()
            .firstOrNull()
            ?: error("No completed workout available")

        val sets = exerciseSetRepository.observeSetsForWorkout(completedWorkout.id)
            .first()
            .filter { it.completedAtEpochMillis != null }

        val totalReps = sets.sumOf { it.reps ?: 0 }
        val totalVolumeKg = sets.fold(0f) { acc, set ->
            val reps = set.reps ?: 0
            val weight = set.weightKg ?: 0f
            acc + (reps * weight)
        }

        val completedAt = completedWorkout.completedAtEpochMillis ?: nowEpochMillis
        val personalRecords = personalRecordRepository.observeRecordsInRange(
            userId = user.id,
            startEpochMillis = completedAt - ONE_DAY_MILLIS,
            endEpochMillis = completedAt + ONE_DAY_MILLIS,
        ).first()

        return WorkoutSummaryData(
            workoutId = completedWorkout.id,
            workoutName = completedWorkout.name,
            completedAtEpochMillis = completedAt,
            estimatedDurationMinutes = completedWorkout.estimatedDurationMinutes,
            estimatedCalories = completedWorkout.estimatedCalories ?: 0,
            totalSets = sets.size,
            totalReps = totalReps,
            totalVolumeKg = totalVolumeKg,
            personalRecordCount = personalRecords.size,
        )
    }

    private companion object {
        const val ONE_DAY_MILLIS = 24L * 60L * 60L * 1000L
    }
}
