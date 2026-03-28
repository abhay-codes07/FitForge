package com.fitforge.app.presentation.dailylog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitforge.app.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLogScreen(
    uiState: DailyLogUiState,
    onAddWater: (Int) -> Unit,
    onDismissError: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onDismissError()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Daily Log") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("dailylog_loading"))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding)
                .testTag("dailylog_screen"),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = "Today's Activity",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            // Water Intake Card
            MetricCard(
                title = "💧 Water Intake",
                current = uiState.waterIntakeMl,
                goal = uiState.waterGoalMl,
                unit = "ml",
                progress = uiState.waterIntakeMl.toFloat() / uiState.waterGoalMl.toFloat(),
                onAction = {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Button(
                            onClick = { onAddWater(250) },
                            modifier = Modifier.testTag("add_water_250"),
                        ) {
                            Text("+ 250ml")
                        }
                        Button(
                            onClick = { onAddWater(500) },
                            modifier = Modifier.testTag("add_water_500"),
                        ) {
                            Text("+ 500ml")
                        }
                    }
                },
            )

            // Steps Card
            MetricCard(
                title = "👣 Steps",
                current = uiState.steps,
                goal = 10000,
                unit = "steps",
                progress = uiState.steps.toFloat() / 10000f,
            )

            // Sleep Card
            if (uiState.sleepMinutes != null) {
                MetricCard(
                    title = "😴 Sleep",
                    current = uiState.sleepMinutes,
                    goal = uiState.sleepGoalMinutes,
                    unit = "min",
                    displayValue = "${uiState.sleepMinutes / 60}h ${uiState.sleepMinutes % 60}m",
                    progress = uiState.sleepMinutes.toFloat() / uiState.sleepGoalMinutes.toFloat(),
                )
            }

            // Active Calories Card
            SimpleMetricCard(
                title = "🔥 Active Calories",
                value = "${uiState.activeCalories} kcal",
            )

            // Workout Minutes Card
            SimpleMetricCard(
                title = "💪 Workout Minutes",
                value = "${uiState.workoutMinutes} min",
            )

            // Distance Card
            SimpleMetricCard(
                title = "🏃 Distance",
                value = "${uiState.distanceKm} km",
            )

            // Heart Rate Cards
            if (uiState.restingHeartRate != null || uiState.averageHeartRate != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    if (uiState.restingHeartRate != null) {
                        SimpleMetricCard(
                            title = "❤️ Resting HR",
                            value = "${uiState.restingHeartRate} bpm",
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (uiState.averageHeartRate != null) {
                        SimpleMetricCard(
                            title = "💓 Avg HR",
                            value = "${uiState.averageHeartRate} bpm",
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Readiness Score
            if (uiState.readinessScore != null) {
                SimpleMetricCard(
                    title = "⚡ Readiness Score",
                    value = "${uiState.readinessScore}/100",
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    current: Int,
    goal: Int,
    unit: String,
    progress: Float,
    displayValue: String? = null,
    onAction: (@Composable () -> Unit)? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("metric_card_$title"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = displayValue ?: "$current $unit",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = "Goal: $goal $unit",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            onAction?.invoke()
        }
    }
}

@Composable
private fun SimpleMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("simple_metric_card_$title"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
