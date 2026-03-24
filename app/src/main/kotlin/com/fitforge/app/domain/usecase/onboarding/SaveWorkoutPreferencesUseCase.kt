package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.repository.OnboardingRepository
import javax.inject.Inject

class SaveWorkoutPreferencesUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke(
        selectedLocations: Set<String>,
        selectedEquipment: Set<String>,
    ) {
        onboardingRepository.setWorkoutLocations(selectedLocations)
        onboardingRepository.setAvailableEquipment(selectedEquipment)
    }
}
