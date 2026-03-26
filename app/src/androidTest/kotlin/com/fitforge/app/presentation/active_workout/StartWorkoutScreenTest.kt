package com.fitforge.app.presentation.active_workout

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Rule
import org.junit.Test

class StartWorkoutScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun startWorkout_rendersOptions() {
        composeRule.setContent {
            FitForgeTheme {
                StartWorkoutScreen(
                    uiState = StartWorkoutUiState(
                        isLoading = false,
                        options = listOf("Start Scheduled Workout", "Quick Workout", "Empty Workout"),
                        scheduledWorkoutCountLabel = "1 scheduled this week",
                    ),
                    onOptionSelected = {},
                )
            }
        }

        composeRule.onNodeWithTag("start_workout_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("start_workout_scheduled_count").assertIsDisplayed()
        composeRule.onNodeWithTag("start_option_Start Scheduled Workout").assertIsDisplayed()
    }

    @Test
    fun startWorkout_showsLoading() {
        composeRule.setContent {
            FitForgeTheme {
                StartWorkoutScreen(
                    uiState = StartWorkoutUiState(isLoading = true),
                    onOptionSelected = {},
                )
            }
        }

        composeRule.onNodeWithTag("start_workout_loading").assertIsDisplayed()
    }
}
