package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.presentation.onboarding.bodymetrics.BodyMetricsGender
import com.fitforge.app.presentation.onboarding.bodymetrics.BodyMetricsUnitSystem
import javax.inject.Inject

data class BodyMetricsDraft(
    val unitSystem: BodyMetricsUnitSystem,
    val height: String,
    val weight: String,
    val age: String,
    val gender: BodyMetricsGender,
)

class GetBodyMetricsDraftUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke(): BodyMetricsDraft {
        val unitSystem = when (onboardingRepository.getPreferredUnitSystem()) {
            BodyMetricsUnitSystem.Imperial.storageValue -> BodyMetricsUnitSystem.Imperial
            else -> BodyMetricsUnitSystem.Metric
        }
        val gender = BodyMetricsGender.entries.firstOrNull {
            it.storageValue == onboardingRepository.getGenderValue()
        } ?: BodyMetricsGender.Unspecified

        return BodyMetricsDraft(
            unitSystem = unitSystem,
            height = onboardingRepository.getHeightValue()?.stripTrailingZero().orEmpty(),
            weight = onboardingRepository.getWeightValue()?.stripTrailingZero().orEmpty(),
            age = onboardingRepository.getAgeValue()?.stripTrailingZero().orEmpty(),
            gender = gender,
        )
    }

    private fun Float.stripTrailingZero(): String {
        return if (this % 1f == 0f) toInt().toString() else toString()
    }
}

