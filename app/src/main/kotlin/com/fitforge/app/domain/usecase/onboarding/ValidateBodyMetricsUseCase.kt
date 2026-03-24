package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.presentation.onboarding.bodymetrics.BodyMetricsUnitSystem
import javax.inject.Inject

data class BodyMetricsValidationResult(
    val isValid: Boolean,
    val heightError: String? = null,
    val weightError: String? = null,
    val ageError: String? = null,
)

class ValidateBodyMetricsUseCase @Inject constructor() {
    operator fun invoke(
        unitSystem: BodyMetricsUnitSystem,
        height: String,
        weight: String,
        age: String,
    ): BodyMetricsValidationResult {
        val heightValue = height.toFloatOrNull()
        val weightValue = weight.toFloatOrNull()
        val ageValue = age.toFloatOrNull()

        val heightError = when {
            heightValue == null -> "Enter a valid height"
            unitSystem == BodyMetricsUnitSystem.Metric && heightValue !in 100f..250f -> "Height must be between 100 and 250 cm"
            unitSystem == BodyMetricsUnitSystem.Imperial && heightValue !in 39f..98f -> "Height must be between 39 and 98 in"
            else -> null
        }

        val weightError = when {
            weightValue == null -> "Enter a valid weight"
            unitSystem == BodyMetricsUnitSystem.Metric && weightValue !in 30f..300f -> "Weight must be between 30 and 300 kg"
            unitSystem == BodyMetricsUnitSystem.Imperial && weightValue !in 66f..660f -> "Weight must be between 66 and 660 lb"
            else -> null
        }

        val ageError = when {
            ageValue == null -> "Enter a valid age"
            ageValue !in 13f..100f -> "Age must be between 13 and 100"
            else -> null
        }

        return BodyMetricsValidationResult(
            isValid = heightError == null && weightError == null && ageError == null,
            heightError = heightError,
            weightError = weightError,
            ageError = ageError,
        )
    }
}

