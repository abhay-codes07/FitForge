package com.fitforge.app.presentation.programs

import com.fitforge.app.data.local.db.entity.ProgramEntity

data class ProgramsUiState(
    val isLoading: Boolean = true,
    val programs: List<ProgramEntity> = emptyList(),
    val selectedGoal: String = "all",
    val goals: List<String> = listOf("all", "weight_loss", "muscle_gain", "strength", "endurance", "flexibility"),
    val errorMessage: String? = null,
)
