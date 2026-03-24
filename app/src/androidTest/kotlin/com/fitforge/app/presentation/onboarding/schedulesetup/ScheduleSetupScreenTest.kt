package com.fitforge.app.presentation.onboarding.schedulesetup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.semantics.SemanticsActions
import com.fitforge.app.domain.usecase.onboarding.GetScheduleOptionsUseCase
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ScheduleSetupScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun scheduleSetup_selectsDaysAdjustsDurationAndContinues() {
        var continueClicks = 0
        val dayOptions = GetScheduleOptionsUseCase()()

        composeRule.setContent {
            var uiState by mutableStateOf(ScheduleSetupUiState(dayOptions = dayOptions))

            FitForgeTheme {
                ScheduleSetupScreen(
                    uiState = uiState,
                    onDayToggle = {
                        val next = uiState.selectedDays.toMutableSet().apply {
                            if (!add(it)) remove(it)
                        }
                        uiState = uiState.copy(selectedDays = next)
                    },
                    onDurationChanged = { uiState = uiState.copy(durationMinutes = it.toInt()) },
                    onContinue = { continueClicks++ },
                    onNavigateNext = { },
                )
            }
        }

        composeRule.onNodeWithTag("schedule_setup_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("day_mon").performClick()
        composeRule.onNodeWithTag("schedule_duration_slider")
            .performSemanticsAction(SemanticsActions.SetProgress) { it(60f) }
        composeRule.onNodeWithTag("schedule_setup_continue").performClick()

        assertEquals(1, continueClicks)
    }
}
