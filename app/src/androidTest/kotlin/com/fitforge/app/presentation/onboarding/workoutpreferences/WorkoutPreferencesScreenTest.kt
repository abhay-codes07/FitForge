package com.fitforge.app.presentation.onboarding.workoutpreferences

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.fitforge.app.domain.usecase.onboarding.GetWorkoutPreferencesOptionsUseCase
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class WorkoutPreferencesScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun workoutPreferences_selectsLocationAndEquipmentAndContinues() {
        var continueClicks = 0
        val (locationOptions, equipmentOptions) = GetWorkoutPreferencesOptionsUseCase()()

        composeRule.setContent {
            var uiState by mutableStateOf(
                WorkoutPreferencesUiState(
                    locationOptions = locationOptions,
                    equipmentOptions = equipmentOptions,
                )
            )

            FitForgeTheme {
                WorkoutPreferencesScreen(
                    uiState = uiState,
                    onLocationToggle = {
                        val next = uiState.selectedLocations.toMutableSet().apply {
                            if (!add(it)) remove(it)
                        }
                        uiState = uiState.copy(selectedLocations = next)
                    },
                    onEquipmentToggle = {
                        val next = uiState.selectedEquipment.toMutableSet().apply {
                            if (it == "none") {
                                clear()
                                add(it)
                            } else {
                                remove("none")
                                if (!add(it)) remove(it)
                            }
                        }
                        uiState = uiState.copy(selectedEquipment = next)
                    },
                    onContinue = { continueClicks++ },
                    onNavigateNext = { },
                )
            }
        }

        composeRule.onNodeWithTag("workout_preferences_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("location_home").performClick()
        composeRule.onNodeWithTag("equipment_dumbbells").performClick()
        composeRule.onNodeWithTag("workout_preferences_continue").performClick()

        assertEquals(1, continueClicks)
    }
}
