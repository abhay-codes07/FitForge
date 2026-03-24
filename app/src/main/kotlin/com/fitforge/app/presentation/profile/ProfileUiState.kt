package com.fitforge.app.presentation.profile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val displayName: String = "Athlete",
    val email: String = "Not linked",
    val fitnessLevel: String = "unspecified",
    val goals: List<String> = emptyList(),
    val themePreference: String = "system",
    val preferredUnitSystem: String = "metric",
    val notificationsEnabled: Boolean = false,
    val errorMessage: String? = null,
)
