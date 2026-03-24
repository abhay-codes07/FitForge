package com.fitforge.app.presentation.onboarding.workoutpreferences

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitforge.app.domain.usecase.onboarding.EquipmentOption
import com.fitforge.app.domain.usecase.onboarding.WorkoutLocationOption
import com.fitforge.app.presentation.theme.Mint500
import com.fitforge.app.presentation.theme.Purple100
import com.fitforge.app.presentation.theme.Purple400
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Spacing

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun WorkoutPreferencesScreen(
    uiState: WorkoutPreferencesUiState,
    onLocationToggle: (String) -> Unit,
    onEquipmentToggle: (String) -> Unit,
    onContinue: () -> Unit,
    onNavigateNext: (String) -> Unit,
) {
    LaunchedEffect(uiState.destinationRoute) {
        uiState.destinationRoute?.let(onNavigateNext)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("workout_preferences_screen"),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.xl),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Text(
                        text = "Where do you usually train?",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "Pick every setting that fits your routine. FitForge will prioritize workouts that match your environment.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        uiState.locationOptions.forEach { option ->
                            PreferenceCard(
                                tag = "location_${option.storageValue}",
                                title = option.title,
                                description = option.description,
                                selected = option.storageValue in uiState.selectedLocations,
                                onClick = { onLocationToggle(option.storageValue) },
                            )
                        }
                    }
                    uiState.locationError?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.testTag("workout_location_error"),
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Text(
                        text = "Available equipment",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        uiState.equipmentOptions.forEach { option ->
                            EquipmentChip(
                                option = option,
                                selected = option.storageValue in uiState.selectedEquipment,
                                onClick = { onEquipmentToggle(option.storageValue) },
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onContinue,
                enabled = uiState.canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("workout_preferences_continue"),
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
private fun PreferenceCard(
    tag: String,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Purple600 else Purple100,
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = if (selected) MaterialTheme.colorScheme.onPrimary else Purple600,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) Purple400 else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.76f),
            )
        }
    }
}

@Composable
private fun EquipmentChip(
    option: EquipmentOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val background = if (selected) Purple600 else Purple100
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimary else Purple600

    Text(
        text = option.title,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable(onClick = onClick)
            .testTag("equipment_${option.storageValue}")
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        color = textColor,
        style = MaterialTheme.typography.labelLarge,
    )
}
