package com.fitforge.app.presentation.onboarding.welcome

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.fitforge.app.domain.model.onboarding.WelcomePage
import com.fitforge.app.presentation.theme.FitForgeTheme
import com.fitforge.app.presentation.theme.Mint500
import com.fitforge.app.presentation.theme.Purple600
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class WelcomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun welcomeScreen_displaysPagerAndInvokesGetStarted() {
        val pages = listOf(
            WelcomePage("Page One", "Description One", listOf(Purple600, Mint500), "Stat", "1"),
            WelcomePage("Page Two", "Description Two", listOf(Purple600, Mint500), "Stat", "2"),
            WelcomePage("Page Three", "Description Three", listOf(Purple600, Mint500), "Stat", "3"),
        )
        var getStartedClicks = 0

        composeRule.setContent {
            FitForgeTheme {
                WelcomeScreen(
                    uiState = WelcomeUiState(pages = pages),
                    onGetStarted = { getStartedClicks++ },
                    onNavigateNext = { },
                )
            }
        }

        composeRule.onNodeWithTag("welcome_screen").assertIsDisplayed()
        composeRule.onNodeWithText("Page One").assertIsDisplayed()
        composeRule.onNodeWithTag("welcome_pager").performTouchInput { swipeLeft() }
        composeRule.onNodeWithText("Page Two").assertIsDisplayed()
        composeRule.onNodeWithTag("welcome_get_started").performClick()

        assertEquals(1, getStartedClicks)
    }
}
