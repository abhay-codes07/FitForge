package com.fitforge.app.presentation.active_workout

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Rule
import org.junit.Test

class ActiveWorkoutScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun activeWorkout_rendersContent() {
        composeRule.setContent {
            FitForgeTheme {
                ActiveWorkoutScreen(
                    uiState = ActiveWorkoutUiState(
                        isLoading = false,
                        workoutTitle = "Leg Day",
                        exerciseName = "Squat",
                        currentExerciseIndex = 0,
                        totalExercises = 3,
                        currentSet = 1,
                        targetSets = 3,
                        repCount = 12,
                        targetReps = 12,
                    ),
                    onClose = {},
                    onShuffle = {},
                    onSkip = {},
                    onRepIncrement = {},
                    onRepDecrement = {},
                    onCompleteSet = {},
                    onDismissRestTimer = {},
                    onRetry = {},
                    onDismissError = {},
                    timerServiceEnabled = false,
                )
            }
        }

        composeRule.onNodeWithTag("active_workout_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("active_workout_exercise_name").assertIsDisplayed()
        composeRule.onNodeWithTag("active_workout_rep_counter").assertIsDisplayed()
        composeRule.onNodeWithTag("active_workout_progress").assertIsDisplayed()
    }

    @Test
    fun activeWorkout_showsLoadingState() {
        composeRule.setContent {
            FitForgeTheme {
                ActiveWorkoutScreen(
                    uiState = ActiveWorkoutUiState(isLoading = true),
                    onClose = {},
                    onShuffle = {},
                    onSkip = {},
                    onRepIncrement = {},
                    onRepDecrement = {},
                    onCompleteSet = {},
                    onDismissRestTimer = {},
                    onRetry = {},
                    onDismissError = {},
                    timerServiceEnabled = false,
                )
            }
        }

        composeRule.onNodeWithTag("active_workout_loading").assertIsDisplayed()
    }
}
