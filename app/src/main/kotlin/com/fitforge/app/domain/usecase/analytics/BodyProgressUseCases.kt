package com.fitforge.app.domain.usecase.analytics

import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import com.fitforge.app.domain.repository.BodyMeasurementRepository
import com.fitforge.app.domain.repository.UserRepository
import java.util.TimeZone
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.first

data class BodyProgressData(
    val isGuest: Boolean,
    val latestWeightKg: Float?,
    val latestBodyFatPercent: Float?,
    val weightTrend: List<Float>,
    val bodyFatTrend: List<Float>,
    val photoUris: List<String>,
    val entries: List<BodyMeasurementEntity>,
)

class GetBodyProgressDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val bodyMeasurementRepository: BodyMeasurementRepository,
) {
    suspend operator fun invoke(
        rangeDays: Int,
        nowEpochMillis: Long = System.currentTimeMillis(),
        timezone: TimeZone = TimeZone.getDefault(),
    ): BodyProgressData {
        val user = userRepository.getPrimaryUser()
            ?: return BodyProgressData(
                isGuest = true,
                latestWeightKg = null,
                latestBodyFatPercent = null,
                weightTrend = emptyList(),
                bodyFatTrend = emptyList(),
                photoUris = emptyList(),
                entries = emptyList(),
            )

        val _offsetMillis = timezone.getOffset(nowEpochMillis).toLong()
        val startMillis = nowEpochMillis - TimeUnit.DAYS.toMillis(rangeDays.toLong())
        val entries = bodyMeasurementRepository.observeMeasurementsInRange(
            userId = user.id,
            startEpochMillis = startMillis,
            endEpochMillis = nowEpochMillis,
        ).first().sortedBy { it.recordedAtEpochMillis }

        val latest = entries.lastOrNull()
        return BodyProgressData(
            isGuest = false,
            latestWeightKg = latest?.weightKg,
            latestBodyFatPercent = latest?.bodyFatPercent,
            weightTrend = entries.mapNotNull { it.weightKg },
            bodyFatTrend = entries.mapNotNull { it.bodyFatPercent },
            photoUris = entries.flatMap { it.progressPhotoUris }.distinct(),
            entries = entries,
        )
    }
}

class AddBodyMeasurementUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val bodyMeasurementRepository: BodyMeasurementRepository,
) {
    suspend operator fun invoke(
        weightKg: Float?,
        bodyFatPercent: Float?,
        photoUri: String?,
        notes: String?,
        nowEpochMillis: Long = System.currentTimeMillis(),
    ) {
        require(weightKg != null || bodyFatPercent != null || !photoUri.isNullOrBlank()) {
            "Add at least one value (weight, body fat, or photo)."
        }

        val user = userRepository.getPrimaryUser()
            ?: error("No active user")

        val entity = BodyMeasurementEntity(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            recordedAtEpochMillis = nowEpochMillis,
            weightKg = weightKg,
            bodyFatPercent = bodyFatPercent,
            chestCm = null,
            waistCm = null,
            hipsCm = null,
            bicepsCm = null,
            thighCm = null,
            calfCm = null,
            neckCm = null,
            progressPhotoUris = listOfNotNull(photoUri?.takeIf { it.isNotBlank() }),
            notes = notes,
        )

        bodyMeasurementRepository.upsertMeasurement(entity)
    }
}
