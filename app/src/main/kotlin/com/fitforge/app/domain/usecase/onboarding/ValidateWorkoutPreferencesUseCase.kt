package com.fitforge.app.domain.usecase.onboarding

import javax.inject.Inject

data class WorkoutPreferencesValidationResult(
    val isValid: Boolean,
    val locationError: String? = null,
)

class ValidateWorkoutPreferencesUseCase @Inject constructor() {
    operator fun invoke(selectedLocations: Set<String>): WorkoutPreferencesValidationResult {
        return if (selectedLocations.isEmpty()) {
            WorkoutPreferencesValidationResult(
                isValid = false,
                locationError = "Select at least one workout setting to personalize your plan.",
            )
        } else {
            WorkoutPreferencesValidationResult(isValid = true)
        }
    }
}
