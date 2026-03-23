package com.fitforge.app.presentation.onboarding.bodymetrics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fitforge.app.presentation.theme.Purple100
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Spacing

@Composable
fun BodyMetricsScreen(
    uiState: BodyMetricsUiState,
    onUnitSystemSelected: (BodyMetricsUnitSystem) -> Unit,
    onHeightChanged: (String) -> Unit,
    onWeightChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
    onGenderSelected: (BodyMetricsGender) -> Unit,
    onContinue: () -> Unit,
    onNavigateNext: (String) -> Unit,
) {
    LaunchedEffect(uiState.destinationRoute) {
        uiState.destinationRoute?.let(onNavigateNext)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("body_metrics_screen"),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.xl),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                Text(
                    text = "Tell FitForge about your baseline",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "These numbers help personalize calorie targets, workout volume, and progress tracking.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    UnitChip(
                        label = "Metric",
                        selected = uiState.unitSystem == BodyMetricsUnitSystem.Metric,
                        onClick = { onUnitSystemSelected(BodyMetricsUnitSystem.Metric) },
                        tag = "unit_metric",
                    )
                    UnitChip(
                        label = "Imperial",
                        selected = uiState.unitSystem == BodyMetricsUnitSystem.Imperial,
                        onClick = { onUnitSystemSelected(BodyMetricsUnitSystem.Imperial) },
                        tag = "unit_imperial",
                    )
                }

                OutlinedTextField(
                    value = uiState.height,
                    onValueChange = onHeightChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_height"),
                    label = {
                        Text(if (uiState.unitSystem == BodyMetricsUnitSystem.Metric) "Height (cm)" else "Height (in)")
                    },
                    isError = uiState.heightError != null,
                    supportingText = {
                        uiState.heightError?.let { Text(it) }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                OutlinedTextField(
                    value = uiState.weight,
                    onValueChange = onWeightChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_weight"),
                    label = {
                        Text(if (uiState.unitSystem == BodyMetricsUnitSystem.Metric) "Weight (kg)" else "Weight (lb)")
                    },
                    isError = uiState.weightError != null,
                    supportingText = {
                        uiState.weightError?.let { Text(it) }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                OutlinedTextField(
                    value = uiState.age,
                    onValueChange = onAgeChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_age"),
                    label = { Text("Age") },
                    isError = uiState.ageError != null,
                    supportingText = {
                        uiState.ageError?.let { Text(it) }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Text(
                        text = "Gender",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        BodyMetricsGender.entries.forEach { gender ->
                            UnitChip(
                                label = gender.label,
                                selected = uiState.gender == gender,
                                onClick = { onGenderSelected(gender) },
                                tag = "gender_${gender.storageValue}",
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
                    .testTag("body_metrics_continue"),
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
private fun UnitChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    tag: String,
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(999.dp),
        color = if (selected) Purple600 else Purple100,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
            color = if (selected) MaterialTheme.colorScheme.onPrimary else Purple600,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
