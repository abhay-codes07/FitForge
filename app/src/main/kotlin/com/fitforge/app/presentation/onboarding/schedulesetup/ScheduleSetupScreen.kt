package com.fitforge.app.presentation.onboarding.schedulesetup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitforge.app.domain.usecase.onboarding.WorkoutDayOption
import com.fitforge.app.presentation.theme.Purple100
import com.fitforge.app.presentation.theme.Purple400
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Spacing

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun ScheduleSetupScreen(
    uiState: ScheduleSetupUiState,
    onDayToggle: (String) -> Unit,
    onDurationChanged: (Float) -> Unit,
    onContinue: () -> Unit,
    onNavigateNext: (String) -> Unit,
) {
    LaunchedEffect(uiState.destinationRoute) {
        uiState.destinationRoute?.let(onNavigateNext)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("schedule_setup_screen"),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.xl),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Text(
                        text = "Build your weekly rhythm",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "Select your preferred training days and how long each session should usually take.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Text(
                        text = "Training days",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        uiState.dayOptions.forEach { option ->
                            DayChip(
                                option = option,
                                selected = option.storageValue in uiState.selectedDays,
                                onClick = { onDayToggle(option.storageValue) },
                            )
                        }
                    }
                    uiState.dayError?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.testTag("schedule_day_error"),
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Text(
                        text = "Session duration",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "${uiState.durationMinutes} min",
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Purple600,
                        modifier = Modifier.testTag("schedule_duration_value"),
                    )
                    Slider(
                        value = uiState.durationMinutes.toFloat(),
                        onValueChange = onDurationChanged,
                        valueRange = 15f..90f,
                        steps = 14,
                        modifier = Modifier.testTag("schedule_duration_slider"),
                    )
                }
            }

            Button(
                onClick = onContinue,
                enabled = uiState.canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("schedule_setup_continue"),
                colors = ButtonDefaults.buttonColors(containerColor = Purple600),
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = Spacing.xxs),
                )
            }
        }
    }
}

@Composable
private fun DayChip(
    option: WorkoutDayOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = option.shortLabel,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) Purple600 else Purple100)
            .clickable(onClick = onClick)
            .testTag("day_${option.storageValue}")
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        color = if (selected) MaterialTheme.colorScheme.onPrimary else Purple400,
        style = MaterialTheme.typography.labelLarge,
    )
}
