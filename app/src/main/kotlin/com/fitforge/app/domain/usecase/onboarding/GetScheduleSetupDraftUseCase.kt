package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.repository.OnboardingRepository
import javax.inject.Inject

data class ScheduleSetupDraft(
    val selectedDays: Set<String>,
    val durationMinutes: Int,
)

class GetScheduleSetupDraftUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke(): ScheduleSetupDraft = ScheduleSetupDraft(
        selectedDays = onboardingRepository.getWorkoutDays(),
        durationMinutes = onboardingRepository.getWorkoutDurationMinutes(),
    )
}
