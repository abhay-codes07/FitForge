package com.fitforge.app.presentation.analytics

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.fitforge.app.presentation.theme.FitForgeTheme
import org.junit.Rule
import org.junit.Test

class BodyProgressScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun bodyProgress_rendersAndSaves() {
        composeRule.setContent {
            FitForgeTheme {
                BodyProgressScreen(
                    uiState = BodyProgressUiState(
                        isLoading = false,
                        latestWeightLabel = "78.5 kg",
                        latestBodyFatLabel = "18.1 %",
                        entries = listOf(
                            BodyMeasurementEntryUi("m1", "01 Jan 2026", "78.5 kg", "18.1 %"),
                        ),
                    ),
                    onRangeSelected = {},
                    onWeightChanged = {},
                    onBodyFatChanged = {},
                    onPhotoUriChanged = {},
                    onNoteChanged = {},
                    onSaveMeasurement = {},
                )
            }
        }

        composeRule.onNodeWithTag("body_progress_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("body_progress_weight_input").performTextInput("77.8")
        composeRule.onNodeWithTag("body_progress_save").performClick()
        composeRule.onNodeWithTag("body_progress_entry_m1").assertIsDisplayed()
    }

    @Test
    fun bodyProgress_showsLoading() {
        composeRule.setContent {
            FitForgeTheme {
                BodyProgressScreen(
                    uiState = BodyProgressUiState(isLoading = true),
                    onRangeSelected = {},
                    onWeightChanged = {},
                    onBodyFatChanged = {},
                    onPhotoUriChanged = {},
                    onNoteChanged = {},
                    onSaveMeasurement = {},
                )
            }
        }

        composeRule.onNodeWithTag("body_progress_loading").assertIsDisplayed()
    }
}
