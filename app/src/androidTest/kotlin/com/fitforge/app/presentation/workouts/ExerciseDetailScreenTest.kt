package com.fitforge.app.presentation.workouts

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Rule
import org.junit.Test

class ExerciseDetailScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun exerciseDetail_rendersContent() {
        composeRule.setContent {
            FitForgeTheme {
                ExerciseDetailScreen(
                    uiState = ExerciseDetailUiState(
                        isLoading = false,
                        title = "Bench Press",
                        description = "Chest compound movement",
                        category = "Strength",
                        difficulty = "Intermediate",
                        durationLabel = "15 min",
                        caloriesLabel = "120 kcal",
                        equipment = listOf("Barbell", "Bench"),
                        primaryMuscles = listOf("Chest", "Triceps"),
                        instructions = listOf("Set grip", "Lower bar", "Press up"),
                    ),
                )
            }
        }

        composeRule.onNodeWithTag("exercise_detail_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("exercise_detail_title").assertIsDisplayed()
        composeRule.onNodeWithTag("exercise_detail_instructions").assertIsDisplayed()
    }

    @Test
    fun exerciseDetail_showsLoading() {
        composeRule.setContent {
            FitForgeTheme {
                ExerciseDetailScreen(uiState = ExerciseDetailUiState(isLoading = true))
            }
        }

        composeRule.onNodeWithTag("exercise_detail_loading").assertIsDisplayed()
    }
}
