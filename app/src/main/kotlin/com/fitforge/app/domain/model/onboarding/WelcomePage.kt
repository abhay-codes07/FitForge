package com.fitforge.app.domain.model.onboarding

import androidx.compose.ui.graphics.Color

data class WelcomePage(
    val title: String,
    val description: String,
    val accentColors: List<Color>,
    val statLabel: String,
    val statValue: String,
)

