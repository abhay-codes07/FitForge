package com.fitforge.app.presentation.onboarding.splash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Rule
import org.junit.Test

class SplashScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun splashScreen_displaysBrandingContent() {
        composeRule.setContent {
            FitForgeTheme {
                SplashScreen(
                    uiState = SplashUiState(),
                    onSplashFinished = { },
                )
            }
        }

        composeRule.onNodeWithTag("splash_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("splash_logo").assertIsDisplayed()
        composeRule.onNodeWithTag("splash_title").assertIsDisplayed()
        composeRule.onNodeWithTag("splash_tagline").assertIsDisplayed()
    }
}
