package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.repository.OnboardingRepository
import javax.inject.Inject

class SaveFitnessLevelUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke(value: String) {
        onboardingRepository.setFitnessLevel(value)
    }
}
