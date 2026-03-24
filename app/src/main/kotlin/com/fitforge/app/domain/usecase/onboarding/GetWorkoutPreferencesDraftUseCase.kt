package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.repository.OnboardingRepository
import javax.inject.Inject

data class WorkoutPreferencesDraft(
    val selectedLocations: Set<String>,
    val selectedEquipment: Set<String>,
)

class GetWorkoutPreferencesDraftUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke(): WorkoutPreferencesDraft = WorkoutPreferencesDraft(
        selectedLocations = onboardingRepository.getWorkoutLocations(),
        selectedEquipment = onboardingRepository.getAvailableEquipment(),
    )
}
