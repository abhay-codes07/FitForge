package com.fitforge.app.presentation.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeDashboardScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun homeDashboard_rendersStatsAndRefreshes() {
        var refreshClicks = 0

        composeRule.setContent {
            FitForgeTheme {
                HomeDashboardScreen(
                    uiState = HomeDashboardUiState(
                        isLoading = false,
                        greetingName = "Abhay",
                        steps = 10000,
                        activeCalories = 620,
                        workoutMinutes = 70,
                        waterIntakeMl = 2500,
                        latestWeightLabel = "78.5 kg",
                        nextWorkoutTitle = "Leg Day",
                        recentWorkoutTitles = listOf("Leg Day", "Run Intervals"),
                    ),
                    onRefresh = { refreshClicks++ },
                )
            }
        }

        composeRule.onNodeWithTag("home_dashboard_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("home_greeting").assertIsDisplayed()
        composeRule.onNodeWithTag("home_steps_value").assertIsDisplayed()
        composeRule.onNodeWithTag("home_next_workout").assertIsDisplayed()
        composeRule.onNodeWithTag("home_recent_workout_0").assertIsDisplayed()

        composeRule.onNodeWithTag("home_refresh").performClick()
        assertEquals(1, refreshClicks)
    }

    @Test
    fun homeDashboard_showsLoadingState() {
        composeRule.setContent {
            FitForgeTheme {
                HomeDashboardScreen(
                    uiState = HomeDashboardUiState(isLoading = true),
                    onRefresh = { },
                )
            }
        }

        composeRule.onNodeWithTag("home_loading").assertIsDisplayed()
    }
}
