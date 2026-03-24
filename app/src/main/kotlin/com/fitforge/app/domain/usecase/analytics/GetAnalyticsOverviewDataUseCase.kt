package com.fitforge.app.domain.usecase.analytics

import com.fitforge.app.domain.repository.BodyMeasurementRepository
import com.fitforge.app.domain.repository.DailyLogRepository
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.first

data class AnalyticsOverviewData(
    val isGuest: Boolean,
    val rangeDays: Int,
    val totalSteps: Int,
    val totalCalories: Int,
    val totalWorkoutMinutes: Int,
    val completedWorkouts: Int,
    val averageWorkoutMinutes: Int,
    val weightChangeKg: Float?,
    val stepTrend: List<Int>,
)

class GetAnalyticsOverviewDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val dailyLogRepository: DailyLogRepository,
    private val workoutRepository: WorkoutRepository,
    private val bodyMeasurementRepository: BodyMeasurementRepository,
) {
    suspend operator fun invoke(
        rangeDays: Int,
        nowEpochMillis: Long = System.currentTimeMillis(),
        timezone: TimeZone = TimeZone.getDefault(),
    ): AnalyticsOverviewData {
        val user = userRepository.getPrimaryUser()
            ?: return AnalyticsOverviewData(
                isGuest = true,
                rangeDays = rangeDays,
                totalSteps = 0,
                totalCalories = 0,
                totalWorkoutMinutes = 0,
                completedWorkouts = 0,
                averageWorkoutMinutes = 0,
                weightChangeKg = null,
                stepTrend = emptyList(),
            )

        val offsetMillis = timezone.getOffset(nowEpochMillis).toLong()
        val endEpochDay = TimeUnit.MILLISECONDS.toDays(nowEpochMillis + offsetMillis)
        val startEpochDay = endEpochDay - rangeDays + 1
        val dayMillis = TimeUnit.DAYS.toMillis(1)
        val rangeStartMillis = nowEpochMillis - TimeUnit.DAYS.toMillis(rangeDays.toLong())

        val logs = dailyLogRepository.getLogsInRange(user.id, startEpochDay, endEpochDay)
        val completedInRange = workoutRepository.observeWorkoutsByStatus(user.id, "completed").first()
            .count { workout ->
                val completedAt = workout.completedAtEpochMillis ?: return@count false
                completedAt in rangeStartMillis..nowEpochMillis
            }
        val measurements = bodyMeasurementRepository.observeMeasurementsInRange(
            userId = user.id,
            startEpochMillis = rangeStartMillis,
            endEpochMillis = nowEpochMillis,
        ).first().sortedBy { it.recordedAtEpochMillis }

        val stepByDay = logs.associateBy { it.logDateEpochDay }
        val trend = (0 until minOf(7, rangeDays)).map { index ->
            val day = endEpochDay - (minOf(7, rangeDays) - 1 - index)
            stepByDay[day]?.steps ?: 0
        }

        val totalWorkoutMinutes = logs.sumOf { it.workoutMinutes }
        val weightChange = if (measurements.size >= 2) {
            val first = measurements.first().weightKg
            val last = measurements.last().weightKg
            if (first != null && last != null) last - first else null
        } else {
            null
        }

        return AnalyticsOverviewData(
            isGuest = false,
            rangeDays = rangeDays,
            totalSteps = logs.sumOf { it.steps },
            totalCalories = logs.sumOf { it.activeCalories },
            totalWorkoutMinutes = totalWorkoutMinutes,
            completedWorkouts = completedInRange,
            averageWorkoutMinutes = if (logs.isEmpty()) 0 else totalWorkoutMinutes / logs.size,
            weightChangeKg = weightChange,
            stepTrend = trend,
        )
    }
}
