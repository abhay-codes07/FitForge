package com.fitforge.app.domain.usecase.home

import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import com.fitforge.app.data.local.db.entity.DailyLogEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.domain.repository.BodyMeasurementRepository
import com.fitforge.app.domain.repository.DailyLogRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

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
