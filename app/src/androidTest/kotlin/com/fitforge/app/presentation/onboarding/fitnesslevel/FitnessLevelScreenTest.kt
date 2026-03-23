package com.fitforge.app.presentation.onboarding.fitnesslevel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.fitforge.app.domain.usecase.onboarding.GetFitnessLevelOptionsUseCase
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FitnessLevelScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun fitnessLevel_selectsCardAndContinues() {
        var continueClicks = 0
        val options = GetFitnessLevelOptionsUseCase()()

        composeRule.setContent {
            var uiState by mutableStateOf(FitnessLevelUiState(options = options))

            FitForgeTheme {
                FitnessLevelScreen(
                    uiState = uiState,
                    onFitnessLevelSelected = { uiState = uiState.copy(selectedLevel = it) },
                    onContinue = { continueClicks++ },
                    onNavigateNext = { },
                )
            }
        }

        composeRule.onNodeWithTag("fitness_level_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("fitness_level_advanced").performClick()
        composeRule.onNodeWithTag("fitness_level_continue").performClick()

        assertEquals(1, continueClicks)
    }
}
