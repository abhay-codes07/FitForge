package com.fitforge.app.presentation.onboarding.auth

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

class AuthScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun auth_acceptsGuestContinue() {
        var continueClicks = 0

        composeRule.setContent {
            var uiState by mutableStateOf(AuthUiState())

            FitForgeTheme {
                AuthScreen(
                    uiState = uiState,
                    onEmailChanged = { uiState = uiState.copy(email = it) },
                    onPasswordChanged = { uiState = uiState.copy(password = it) },
                    onEmailContinue = { continueClicks++ },
                    onGoogleIdTokenReceived = { },
                    onGoogleSignInFailed = { },
                    onPasswordReset = { },
                    onGuestContinue = { continueClicks++ },
                    onNavigateNext = { },
                    onNavigationHandled = { },
                )
            }
        }

        composeRule.onNodeWithTag("auth_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("auth_guest_continue").performClick()

        assertEquals(1, continueClicks)
    }

    @Test
    fun auth_acceptsEmailCredentials() {
        var continueClicks = 0

        composeRule.setContent {
            var uiState by mutableStateOf(AuthUiState())

            FitForgeTheme {
                AuthScreen(
                    uiState = uiState,
                    onEmailChanged = { uiState = uiState.copy(email = it) },
                    onPasswordChanged = { uiState = uiState.copy(password = it) },
                    onEmailContinue = { continueClicks++ },
                    onGoogleIdTokenReceived = { },
                    onGoogleSignInFailed = { },
                    onPasswordReset = { },
                    onGuestContinue = { },
                    onNavigateNext = { },
                    onNavigationHandled = { },
                )
            }
        }

        composeRule.onNodeWithTag("auth_email").performTextInput("user@example.com")
        composeRule.onNodeWithTag("auth_password").performTextInput("password123")
        composeRule.onNodeWithTag("auth_email_continue").performClick()

        assertEquals(1, continueClicks)
    }
}
