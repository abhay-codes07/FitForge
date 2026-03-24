package com.fitforge.app.presentation.analytics

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Rule
import org.junit.Test

class AnalyticsOverviewScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun analyticsOverview_rendersAndRangeClick() {
        composeRule.setContent {
            FitForgeTheme {
                AnalyticsOverviewScreen(
                    uiState = AnalyticsOverviewUiState(
                        isLoading = false,
                        totalSteps = "12000",
                        totalCalories = "700 kcal",
                        totalWorkoutMinutes = "90 min",
                        completedWorkouts = "3",
                        averageWorkoutMinutes = "30 min/day",
                        weightChangeLabel = "-0.8 kg",
                        stepTrend = listOf(1000, 2000, 3000),
                    ),
                    onRangeSelected = {},
                    onOpenBodyProgress = {},
                )
            }
        }

        composeRule.onNodeWithTag("analytics_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("analytics_open_body_progress").performClick()
        composeRule.onNodeWithTag("analytics_steps").assertIsDisplayed()
        composeRule.onNodeWithTag("analytics_range_7").performClick()
    }

    @Test
    fun analyticsOverview_showsLoading() {
        composeRule.setContent {
            FitForgeTheme {
                AnalyticsOverviewScreen(
                    uiState = AnalyticsOverviewUiState(isLoading = true),
                    onRangeSelected = {},
                    onOpenBodyProgress = {},
                )
            }
        }

        composeRule.onNodeWithTag("analytics_loading").assertIsDisplayed()
    }
}
