package com.fitforge.app.presentation.active_workout

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Rule
import org.junit.Test

class WorkoutSummaryScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun workoutSummary_rendersContent() {
        composeRule.setContent {
            FitForgeTheme {
                WorkoutSummaryScreen(
                    uiState = WorkoutSummaryUiState(
                        isLoading = false,
                        workoutName = "Pull Day",
                        completedAtLabel = "25 Mar, 10:30 AM",
                        durationLabel = "45 min",
                        caloriesLabel = "380 kcal",
                        totalSetsLabel = "15",
                        totalRepsLabel = "110",
                        totalVolumeLabel = "3200.0 kg",
                        personalRecordsLabel = "2",
                        shareText = "I completed Pull Day",
                    ),
                    onDoneClick = {},
                    onRetry = {},
                    onDismissError = {},
                )
            }
        }

        composeRule.onNodeWithTag("workout_summary_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("workout_summary_completed_at").assertIsDisplayed()
        composeRule.onNodeWithTag("workout_summary_share").assertIsDisplayed()
        composeRule.onNodeWithTag("workout_summary_done").assertIsDisplayed()
    }

    @Test
    fun workoutSummary_showsLoading() {
        composeRule.setContent {
            FitForgeTheme {
                WorkoutSummaryScreen(
                    uiState = WorkoutSummaryUiState(isLoading = true),
                    onDoneClick = {},
                    onRetry = {},
                    onDismissError = {},
                )
            }
        }

        composeRule.onNodeWithTag("workout_summary_loading").assertIsDisplayed()
    }
}
