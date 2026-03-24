package com.fitforge.app.presentation.onboarding.workoutpreferences

import com.fitforge.app.domain.usecase.onboarding.EquipmentOption
import com.fitforge.app.domain.usecase.onboarding.WorkoutLocationOption

data class WorkoutPreferencesUiState(
    val locationOptions: List<WorkoutLocationOption> = emptyList(),
    val equipmentOptions: List<EquipmentOption> = emptyList(),
    val selectedLocations: Set<String> = emptySet(),
    val selectedEquipment: Set<String> = emptySet(),
    val locationError: String? = null,
    val destinationRoute: String? = null,
) {
    val canContinue: Boolean
        get() = selectedLocations.isNotEmpty()
}
