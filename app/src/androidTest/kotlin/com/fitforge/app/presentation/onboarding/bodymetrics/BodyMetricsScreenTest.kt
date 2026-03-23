package com.fitforge.app.presentation.onboarding.bodymetrics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BodyMetricsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun bodyMetrics_acceptsInputAndContinue() {
        var continueClicks = 0

        composeRule.setContent {
            var uiState by mutableStateOf(BodyMetricsUiState())

            FitForgeTheme {
                BodyMetricsScreen(
                    uiState = uiState,
                    onUnitSystemSelected = { uiState = uiState.copy(unitSystem = it) },
                    onHeightChanged = { uiState = uiState.copy(height = it) },
                    onWeightChanged = { uiState = uiState.copy(weight = it) },
                    onAgeChanged = { uiState = uiState.copy(age = it) },
                    onGenderSelected = { uiState = uiState.copy(gender = it) },
                    onContinue = { continueClicks++ },
                    onNavigateNext = { },
                )
            }
        }

        composeRule.onNodeWithTag("body_metrics_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("input_height").performTextInput("175")
        composeRule.onNodeWithTag("input_weight").performTextInput("72")
        composeRule.onNodeWithTag("input_age").performTextInput("28")
        composeRule.onNodeWithTag("gender_female").performClick()
        composeRule.onNodeWithTag("body_metrics_continue").performClick()

        assertEquals(1, continueClicks)
    }
}
