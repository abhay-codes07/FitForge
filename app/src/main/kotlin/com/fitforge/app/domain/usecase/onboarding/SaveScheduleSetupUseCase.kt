package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.repository.OnboardingRepository
import javax.inject.Inject

class SaveScheduleSetupUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke(
        selectedDays: Set<String>,
        durationMinutes: Int,
    ) {
        onboardingRepository.setWorkoutDays(selectedDays)
        onboardingRepository.setWorkoutDurationMinutes(durationMinutes)
    }
}
