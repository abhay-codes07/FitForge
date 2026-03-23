package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.repository.OnboardingRepository
import javax.inject.Inject

class GetSelectedFitnessLevelUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke(): String = onboardingRepository.getFitnessLevel()
}
