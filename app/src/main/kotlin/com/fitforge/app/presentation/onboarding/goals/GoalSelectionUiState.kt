package com.fitforge.app.presentation.onboarding.goals

data class GoalSelectionUiState(
    val options: List<GoalOption> = emptyList(),
    val selectedGoals: Set<String> = emptySet(),
    val canContinue: Boolean = false,
    val destinationRoute: String? = null,
)

