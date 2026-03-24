package com.fitforge.app.presentation.onboarding.fitnesslevel

import com.fitforge.app.domain.usecase.onboarding.FitnessLevelOption

data class FitnessLevelUiState(
    val options: List<FitnessLevelOption> = emptyList(),
    val selectedLevel: String? = null,
    val destinationRoute: String? = null,
) {
    val canContinue: Boolean
        get() = selectedLevel != null
}
