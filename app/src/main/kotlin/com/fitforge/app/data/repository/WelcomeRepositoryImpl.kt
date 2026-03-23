package com.fitforge.app.data.repository

import com.fitforge.app.domain.model.onboarding.WelcomePage
import com.fitforge.app.domain.repository.WelcomeRepository
import com.fitforge.app.presentation.theme.GradientMintAccent
import com.fitforge.app.presentation.theme.GradientPrimaryHero
import com.fitforge.app.presentation.theme.GradientWorkoutRing
import javax.inject.Inject

class WelcomeRepositoryImpl @Inject constructor() : WelcomeRepository {
    override suspend fun getWelcomePages(): List<WelcomePage> {
        return listOf(
            WelcomePage(
                title = "Workouts built around your real schedule",
                description = "Plan sessions that fit your available time, equipment, and recovery so you can keep showing up consistently.",
                accentColors = GradientPrimaryHero,
                statLabel = "Minutes saved",
                statValue = "42",
            ),
            WelcomePage(
                title = "Track progress across every training block",
                description = "See strength, cardio, and daily habit signals in one place instead of stitching together multiple apps.",
                accentColors = GradientMintAccent,
                statLabel = "Health signals",
                statValue = "6",
            ),
            WelcomePage(
                title = "Start strong, adjust fast, recover better",
                description = "Move from onboarding to your first plan with clear goals, realistic preferences, and a setup that evolves with you.",
                accentColors = GradientWorkoutRing,
                statLabel = "Weekly streak",
                statValue = "7d",
            ),
        )
    }
}

