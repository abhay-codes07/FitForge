package com.fitforge.app.domain.usecase.active_workout

import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.first

data class StartWorkoutOptionsData(
    val hasPrimaryUser: Boolean,
    val scheduledWorkoutCount: Int,
    val options: List<String>,
)

class GetStartWorkoutOptionsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(nowEpochMillis: Long = System.currentTimeMillis()): StartWorkoutOptionsData {
        val user = userRepository.getPrimaryUser()
            ?: return StartWorkoutOptionsData(
                hasPrimaryUser = false,
                scheduledWorkoutCount = 0,
                options = listOf("Quick Workout", "Empty Workout", "Run / Walk / Cycle", "Timer Only"),
            )

        val end = nowEpochMillis + TimeUnit.DAYS.toMillis(7)
        val scheduled = workoutRepository.observeScheduledWorkoutsForRange(
            userId = user.id,
            startEpochMillis = nowEpochMillis,
            endEpochMillis = end,
        ).first()

        val base = mutableListOf("Quick Workout", "Empty Workout", "Run / Walk / Cycle", "Timer Only")
        if (scheduled.isNotEmpty()) {
            base.add(0, "Start Scheduled Workout")
        }

        return StartWorkoutOptionsData(
            hasPrimaryUser = true,
            scheduledWorkoutCount = scheduled.size,
            options = base,
        )
    }
}
