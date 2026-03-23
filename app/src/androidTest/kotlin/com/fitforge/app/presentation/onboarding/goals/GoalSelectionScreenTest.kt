package com.fitforge.app.presentation.onboarding.goals

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.fitforge.app.domain.usecase.onboarding.GetGoalOptionsUseCase
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GoalSelectionScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun goalSelection_allowsMultiSelectAndContinue() {
        val options = GetGoalOptionsUseCase()()
        var continueClicks = 0

        composeRule.setContent {
            FitForgeTheme {
                GoalSelectionScreen(
                    uiState = GoalSelectionUiState(
                        options = options,
                        selectedGoals = setOf("weight_loss"),
                        canContinue = true,
                    ),
                    onGoalToggle = { },
                    onContinue = { continueClicks++ },
                    onNavigateNext = { },
                )
            }
        }

        composeRule.onNodeWithTag("goal_selection_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("goal_chip_weight_loss").assertIsDisplayed()
        composeRule.onNodeWithTag("goal_chip_endurance").assertIsDisplayed()
        composeRule.onNodeWithTag("goal_continue").performClick()

        assertEquals(1, continueClicks)
    }
}
