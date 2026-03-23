package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.presentation.onboarding.bodymetrics.BodyMetricsGender
import com.fitforge.app.presentation.onboarding.bodymetrics.BodyMetricsUnitSystem
import javax.inject.Inject

class SaveBodyMetricsUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke(
        unitSystem: BodyMetricsUnitSystem,
        height: String,
        weight: String,
        age: String,
        gender: BodyMetricsGender,
    ) {
        onboardingRepository.setPreferredUnitSystem(unitSystem.storageValue)
        onboardingRepository.setHeightValue(height.toFloat())
        onboardingRepository.setWeightValue(weight.toFloat())
        onboardingRepository.setAgeValue(age.toFloat())
        onboardingRepository.setGenderValue(gender.storageValue)
    }
}
