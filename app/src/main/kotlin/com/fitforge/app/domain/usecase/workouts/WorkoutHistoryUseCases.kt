package com.fitforge.app.domain.usecase.workouts

import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class GetWorkoutHistoryUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<WorkoutEntity>> {
        val user = userRepository.observePrimaryUser()
        return user.map { u ->
            if (u != null) {
                workoutRepository.observeWorkoutsByStatus(u.id, "completed")
            } else {
                flowOf(emptyList())
            }
        }.map { flow -> flow.map { it } }.map { emptyList() }
    }

    suspend fun getCompletedWorkouts(): Flow<List<WorkoutEntity>> {
        val user = userRepository.getPrimaryUser() ?: return flowOf(emptyList())
        return workoutRepository.observeWorkoutsByStatus(user.id, "completed")
    }
}

class GetWorkoutStreakUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Result<Int> {
        return runCatching {
            val user = userRepository.getPrimaryUser() ?: return@runCatching 0

            // Get recent completed workouts
            val thirtyDaysAgo = LocalDate.now().minusDays(30).atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC) * 1000
            val endTime = System.currentTimeMillis()

            val recentWorkouts = workoutRepository.observeRecentCompletedWorkouts(user.id, limit = 100)
                .map { workouts ->
                    workouts.filter { it.completedAtEpochMillis != null && it.completedAtEpochMillis!! >= thirtyDaysAgo }
                        .sortedByDescending { it.completedAtEpochMillis }
                }

            // Calculate streak
            var streak = 0
            var currentDate = LocalDate.now()

            recentWorkouts.collect { workouts ->
                val workoutDates = workouts.mapNotNull { workout ->
                    workout.completedAtEpochMillis?.let { millis ->
                        LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                    }
                }.toSet()

                // Check consecutive days
                while (workoutDates.contains(currentDate)) {
                    streak++
                    currentDate = currentDate.minusDays(1)
                }
            }

            streak
        }
    }
}

class GetWorkoutStatisticsUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Result<WorkoutStatistics> {
        return runCatching {
            val user = userRepository.getPrimaryUser()
                ?: return@runCatching WorkoutStatistics()

            var totalWorkouts = 0
            var totalMinutes = 0
            var favoriteType = ""
            val typeCount = mutableMapOf<String, Int>()

            workoutRepository.observeWorkoutsByStatus(user.id, "completed")
                .collect { workouts ->
                    totalWorkouts = workouts.size
                    totalMinutes = workouts.sumOf { it.estimatedDurationMinutes }

                    workouts.forEach { workout ->
                        typeCount[workout.workoutType] = typeCount.getOrDefault(workout.workoutType, 0) + 1
                    }

                    favoriteType = typeCount.maxByOrNull { it.value }?.key ?: ""
                }

            WorkoutStatistics(
                totalWorkouts = totalWorkouts,
                totalMinutes = totalMinutes,
                averageDuration = if (totalWorkouts > 0) totalMinutes / totalWorkouts else 0,
                favoriteWorkoutType = favoriteType,
            )
        }
    }
}

data class WorkoutStatistics(
    val totalWorkouts: Int = 0,
    val totalMinutes: Int = 0,
    val averageDuration: Int = 0,
    val favoriteWorkoutType: String = "",
)
