package com.fitforge.app.presentation.onboarding.welcome

import com.fitforge.app.domain.model.onboarding.WelcomePage

data class WelcomeUiState(
    val pages: List<WelcomePage> = emptyList(),
    val destinationRoute: String? = null,
)

