package com.fitforge.app.presentation.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun profile_rendersAndTogglesSettings() {
        composeRule.setContent {
            FitForgeTheme {
                ProfileScreen(
                    uiState = ProfileUiState(
                        isLoading = false,
                        displayName = "Abhay",
                        email = "abhay@fitforge.dev",
                        fitnessLevel = "intermediate",
                        goals = listOf("muscle_gain"),
                        themePreference = "system",
                        preferredUnitSystem = "metric",
                        notificationsEnabled = true,
                    ),
                    onThemeSelected = {},
                    onUnitSystemSelected = {},
                )
            }
        }

        composeRule.onNodeWithTag("profile_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("profile_name").assertIsDisplayed()
        composeRule.onNodeWithTag("profile_theme_dark").performClick()
        composeRule.onNodeWithTag("profile_units_imperial").performClick()
    }

    @Test
    fun profile_showsLoading() {
        composeRule.setContent {
            FitForgeTheme {
                ProfileScreen(
                    uiState = ProfileUiState(isLoading = true),
                    onThemeSelected = {},
                    onUnitSystemSelected = {},
                )
            }
        }
        composeRule.onNodeWithTag("profile_loading").assertIsDisplayed()
    }
}
