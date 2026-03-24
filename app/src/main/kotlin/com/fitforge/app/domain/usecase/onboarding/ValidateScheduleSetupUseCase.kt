package com.fitforge.app.domain.usecase.onboarding

import javax.inject.Inject

data class ScheduleSetupValidationResult(
    val isValid: Boolean,
    val dayError: String? = null,
)

class ValidateScheduleSetupUseCase @Inject constructor() {
    operator fun invoke(selectedDays: Set<String>): ScheduleSetupValidationResult {
        return if (selectedDays.isEmpty()) {
            ScheduleSetupValidationResult(
                isValid = false,
                dayError = "Choose at least one training day to build your routine.",
            )
        } else {
            ScheduleSetupValidationResult(isValid = true)
        }
    }
}
