package com.fitforge.app.presentation.workouts

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Rule
import org.junit.Test

class WorkoutLibraryScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun workoutLibrary_rendersListAndSearch() {
        composeRule.setContent {
            FitForgeTheme {
                WorkoutLibraryScreen(
                    uiState = WorkoutLibraryUiState(
                        isLoading = false,
                        categoryFilters = listOf("Strength", "Cardio"),
                        difficultyFilters = listOf("Beginner", "Intermediate"),
                        exercises = listOf(
                            WorkoutLibraryExerciseItem(
                                id = "e1",
                                name = "Bench Press",
                                category = "Strength",
                                difficulty = "Intermediate",
                                durationLabel = "10 min",
                                caloriesLabel = "120 kcal",
                                equipment = listOf("Barbell"),
                            ),
                        ),
                    ),
                    onQueryChanged = {},
                    onCategorySelected = {},
                    onDifficultySelected = {},
                )
            }
        }

        composeRule.onNodeWithTag("workout_library_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("workout_search").performTextInput("bench")
        composeRule.onNodeWithTag("workout_item_e1").assertIsDisplayed()
    }

    @Test
    fun workoutLibrary_showsLoadingState() {
        composeRule.setContent {
            FitForgeTheme {
                WorkoutLibraryScreen(
                    uiState = WorkoutLibraryUiState(isLoading = true),
                    onQueryChanged = {},
                    onCategorySelected = {},
                    onDifficultySelected = {},
                )
            }
        }

        composeRule.onNodeWithTag("workout_loading").assertIsDisplayed()
    }
}
