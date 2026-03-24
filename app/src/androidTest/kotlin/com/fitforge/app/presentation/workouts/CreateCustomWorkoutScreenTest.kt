package com.fitforge.app.presentation.workouts

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Rule
import org.junit.Test

class CreateCustomWorkoutScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun createCustomWorkout_rendersAndSubmits() {
        composeRule.setContent {
            FitForgeTheme {
                CreateCustomWorkoutScreen(
                    uiState = CreateCustomWorkoutUiState(
                        isLoading = false,
                        exercises = listOf(
                            CustomWorkoutExerciseItem("e1", "Bench Press", "Strength"),
                        ),
                    ),
                    onNameChanged = {},
                    onDescriptionChanged = {},
                    onSearchQueryChanged = {},
                    onWorkoutTypeSelected = {},
                    onDifficultySelected = {},
                    onDurationChanged = {},
                    onExerciseToggled = {},
                    onSaveClick = {},
                )
            }
        }

        composeRule.onNodeWithTag("custom_workout_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("custom_workout_name").performTextInput("My Workout")
        composeRule.onNodeWithTag("custom_exercise_e1").assertIsDisplayed()
        composeRule.onNodeWithTag("custom_workout_save").performClick()
    }

    @Test
    fun createCustomWorkout_showsLoading() {
        composeRule.setContent {
            FitForgeTheme {
                CreateCustomWorkoutScreen(
                    uiState = CreateCustomWorkoutUiState(isLoading = true),
                    onNameChanged = {},
                    onDescriptionChanged = {},
                    onSearchQueryChanged = {},
                    onWorkoutTypeSelected = {},
                    onDifficultySelected = {},
                    onDurationChanged = {},
                    onExerciseToggled = {},
                    onSaveClick = {},
                )
            }
        }

        composeRule.onNodeWithTag("custom_workout_loading").assertIsDisplayed()
    }
}
