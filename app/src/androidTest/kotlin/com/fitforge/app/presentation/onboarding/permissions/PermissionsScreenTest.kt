package com.fitforge.app.presentation.onboarding.permissions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PermissionsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun permissions_acceptsPermissionDecisionsAndContinues() {
        var continueClicks = 0

        composeRule.setContent {
            var uiState by mutableStateOf(PermissionsUiState())

            FitForgeTheme {
                PermissionsScreen(
                    uiState = uiState,
                    onHealthConnectAvailabilityResolved = { uiState = uiState.copy(isHealthConnectAvailable = it) },
                    onNotificationPermissionResult = {
                        uiState = uiState.copy(
                            notificationState = if (it) UserPrefs.PermissionState.GRANTED else UserPrefs.PermissionState.SKIPPED,
                        )
                    },
                    onNotificationSkipped = {
                        uiState = uiState.copy(notificationState = UserPrefs.PermissionState.SKIPPED)
                    },
                    onHealthConnectPermissionResult = {
                        uiState = uiState.copy(
                            healthConnectState = if (it) UserPrefs.PermissionState.GRANTED else UserPrefs.PermissionState.SKIPPED,
                        )
                    },
                    onHealthConnectSkipped = {
                        uiState = uiState.copy(healthConnectState = UserPrefs.PermissionState.SKIPPED)
                    },
                    onContinue = { continueClicks++ },
                    onNavigateNext = { },
                )
            }
        }

        composeRule.onNodeWithTag("permissions_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("skip_notification_permission").performClick()
        composeRule.onNodeWithTag("skip_health_connect_permission").performClick()
        composeRule.onNodeWithTag("permissions_continue").performClick()

        assertEquals(1, continueClicks)
    }
}
