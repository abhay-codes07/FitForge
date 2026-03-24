package com.fitforge.app.domain.usecase.analytics

import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import com.fitforge.app.data.local.db.entity.DailyLogEntity
import com.fitforge.app.data.local.db.entity.PersonalRecordEntity
import com.fitforge.app.domain.repository.BodyMeasurementRepository
import com.fitforge.app.domain.repository.DailyLogRepository
import com.fitforge.app.domain.repository.PersonalRecordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveDailyLogsInRangeUseCase @Inject constructor(
    private val dailyLogRepository: DailyLogRepository,
) {
    operator fun invoke(
        userId: String,
        startEpochDay: Long,
        endEpochDay: Long,
    ): Flow<List<DailyLogEntity>> = dailyLogRepository.observeLogsInRange(
        userId = userId,
        startEpochDay = startEpochDay,
        endEpochDay = endEpochDay,
    )
}

class ObserveBodyMeasurementsInRangeUseCase @Inject constructor(
    private val bodyMeasurementRepository: BodyMeasurementRepository,
) {
    operator fun invoke(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<List<BodyMeasurementEntity>> = bodyMeasurementRepository.observeMeasurementsInRange(
        userId = userId,
        startEpochMillis = startEpochMillis,
        endEpochMillis = endEpochMillis,
    )
}

class ObservePersonalRecordsInRangeUseCase @Inject constructor(
    private val personalRecordRepository: PersonalRecordRepository,
) {
    operator fun invoke(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Flow<List<PersonalRecordEntity>> = personalRecordRepository.observeRecordsInRange(
        userId = userId,
        startEpochMillis = startEpochMillis,
        endEpochMillis = endEpochMillis,
    )
}

class ObserveTopPersonalRecordForMetricUseCase @Inject constructor(
    private val personalRecordRepository: PersonalRecordRepository,
) {
    operator fun invoke(userId: String, metricType: String): Flow<PersonalRecordEntity?> =
        personalRecordRepository.observeTopRecordForMetric(userId, metricType)
}
