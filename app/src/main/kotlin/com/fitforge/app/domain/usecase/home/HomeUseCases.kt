package com.fitforge.app.domain.usecase.home

import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import com.fitforge.app.data.local.db.entity.DailyLogEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.domain.repository.BodyMeasurementRepository
import com.fitforge.app.domain.repository.DailyLogRepository
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ObserveTodayLogUseCase @Inject constructor(
    private val dailyLogRepository: DailyLogRepository,
) {
    operator fun invoke(userId: String, epochDay: Long): Flow<DailyLogEntity?> =
        dailyLogRepository.observeLogForDay(userId, epochDay)
}

class ObserveRecentCompletedWorkoutsUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    operator fun invoke(userId: String, limit: Int = 5): Flow<List<WorkoutEntity>> =
        workoutRepository.observeRecentCompletedWorkouts(userId, limit)
}

class ObserveNextScheduledWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    operator fun invoke(
        userId: String,
        rangeStartEpochMillis: Long,
        rangeEndEpochMillis: Long,
    ): Flow<WorkoutEntity?> = workoutRepository.observeNextScheduledWorkout(
        userId = userId,
        startEpochMillis = rangeStartEpochMillis,
        endEpochMillis = rangeEndEpochMillis,
    )
}

class ObserveLatestBodyMeasurementUseCase @Inject constructor(
    private val bodyMeasurementRepository: BodyMeasurementRepository,
) {
    operator fun invoke(userId: String): Flow<BodyMeasurementEntity?> =
        bodyMeasurementRepository.observeLatestMeasurement(userId)
}

data class HomeDashboardData(
    val isGuest: Boolean,
    val greetingName: String,
    val steps: Int,
    val activeCalories: Int,
    val workoutMinutes: Int,
    val waterIntakeMl: Int,
    val latestWeightKg: Float?,
    val nextWorkoutTitle: String?,
    val recentWorkoutTitles: List<String>,
)

class GetHomeDashboardDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val dailyLogRepository: DailyLogRepository,
    private val workoutRepository: WorkoutRepository,
    private val bodyMeasurementRepository: BodyMeasurementRepository,
) {
    suspend operator fun invoke(
        nowEpochMillis: Long = System.currentTimeMillis(),
        timezone: TimeZone = TimeZone.getDefault(),
    ): HomeDashboardData {
        val primaryUser = userRepository.getPrimaryUser()
            ?: return HomeDashboardData(
                isGuest = true,
                greetingName = "Athlete",
                steps = 0,
                activeCalories = 0,
                workoutMinutes = 0,
                waterIntakeMl = 0,
                latestWeightKg = null,
                nextWorkoutTitle = null,
                recentWorkoutTitles = emptyList(),
            )

        val offsetMillis = timezone.getOffset(nowEpochMillis).toLong()
        val epochDay = TimeUnit.MILLISECONDS.toDays(nowEpochMillis + offsetMillis)
        val rangeEnd = nowEpochMillis + TimeUnit.DAYS.toMillis(7)

        val todayLog = dailyLogRepository.getLogForDay(primaryUser.id, epochDay)
        val recentWorkouts = workoutRepository.observeRecentCompletedWorkouts(primaryUser.id, limit = 5).first()
        val nextWorkout = workoutRepository.observeNextScheduledWorkout(
            userId = primaryUser.id,
            startEpochMillis = nowEpochMillis,
            endEpochMillis = rangeEnd,
        ).first()
        val latestMeasurement = bodyMeasurementRepository.getLatestMeasurement(primaryUser.id)

        return HomeDashboardData(
            isGuest = false,
            greetingName = primaryUser.displayName,
            steps = todayLog?.steps ?: 0,
            activeCalories = todayLog?.activeCalories ?: 0,
            workoutMinutes = todayLog?.workoutMinutes ?: 0,
            waterIntakeMl = todayLog?.waterIntakeMl ?: 0,
            latestWeightKg = latestMeasurement?.weightKg,
            nextWorkoutTitle = nextWorkout?.name,
            recentWorkoutTitles = recentWorkouts.map { it.name },
        )
    }
}
