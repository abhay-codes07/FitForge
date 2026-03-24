package com.fitforge.app.presentation.analytics

data class BodyMeasurementEntryUi(
    val id: String,
    val recordedAtLabel: String,
    val weightLabel: String,
    val bodyFatLabel: String,
)

data class BodyProgressUiState(
    val isLoading: Boolean = true,
    val selectedRangeDays: Int = 90,
    val latestWeightLabel: String = "--",
    val latestBodyFatLabel: String = "--",
    val weightTrend: List<Float> = emptyList(),
    val bodyFatTrend: List<Float> = emptyList(),
    val photoUris: List<String> = emptyList(),
    val entries: List<BodyMeasurementEntryUi> = emptyList(),
    val weightInput: String = "",
    val bodyFatInput: String = "",
    val photoUriInput: String = "",
    val noteInput: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)
