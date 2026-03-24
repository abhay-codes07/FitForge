package com.fitforge.app.presentation.onboarding.bodymetrics

data class BodyMetricsUiState(
    val unitSystem: BodyMetricsUnitSystem = BodyMetricsUnitSystem.Metric,
    val height: String = "",
    val weight: String = "",
    val age: String = "",
    val gender: BodyMetricsGender = BodyMetricsGender.Unspecified,
    val heightError: String? = null,
    val weightError: String? = null,
    val ageError: String? = null,
    val destinationRoute: String? = null,
) {
    val canContinue: Boolean
        get() = height.isNotBlank() && weight.isNotBlank() && age.isNotBlank()
}

