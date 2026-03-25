package com.fitforge.app.presentation.gps_tracking

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Rule
import org.junit.Test

class GpsTrackingScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun gpsTracking_rendersMainSections() {
        composeRule.setContent {
            FitForgeTheme {
                GpsTrackingScreen(
                    uiState = GpsTrackingUiState(),
                    onModeSelected = {},
                    onStartTracking = {},
                    onStopTracking = {},
                    onServiceStartHandled = {},
                    onServiceStopHandled = {},
                    onServiceStopped = { _, _ -> },
                    onDismissError = {},
                )
            }
        }

        composeRule.onNodeWithTag("gps_tracking_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("gps_route_map").assertIsDisplayed()
        composeRule.onNodeWithTag("gps_toggle_tracking").assertIsDisplayed()
    }

    @Test
    fun gpsTracking_showsModeButtons() {
        composeRule.setContent {
            FitForgeTheme {
                GpsTrackingScreen(
                    uiState = GpsTrackingUiState(),
                    onModeSelected = {},
                    onStartTracking = {},
                    onStopTracking = {},
                    onServiceStartHandled = {},
                    onServiceStopHandled = {},
                    onServiceStopped = { _, _ -> },
                    onDismissError = {},
                )
            }
        }

        composeRule.onNodeWithTag("gps_mode_Run").assertIsDisplayed()
        composeRule.onNodeWithTag("gps_mode_Walk").assertIsDisplayed()
        composeRule.onNodeWithTag("gps_mode_Cycle").assertIsDisplayed()
    }
}
