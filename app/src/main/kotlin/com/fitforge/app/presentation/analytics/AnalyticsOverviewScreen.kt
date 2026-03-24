package com.fitforge.app.presentation.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.fitforge.app.presentation.theme.LightSurface1
import com.fitforge.app.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsOverviewScreen(
    uiState: AnalyticsOverviewUiState,
    onRangeSelected: (Int) -> Unit,
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Analytics") }) }) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("analytics_loading"))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding)
                .testTag("analytics_screen"),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            RangeSelector(selectedRangeDays = uiState.selectedRangeDays, onRangeSelected = onRangeSelected)

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("analytics_error"),
                )
            }

            StatCard("Total Steps", uiState.totalSteps, "analytics_steps")
            StatCard("Active Calories", uiState.totalCalories, "analytics_calories")
            StatCard("Workout Minutes", uiState.totalWorkoutMinutes, "analytics_minutes")
            StatCard("Completed Workouts", uiState.completedWorkouts, "analytics_workouts")
            StatCard("Average Workout", uiState.averageWorkoutMinutes, "analytics_average")
            StatCard("Weight Change", uiState.weightChangeLabel, "analytics_weight_change")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("analytics_step_trend"),
                colors = CardDefaults.cardColors(containerColor = LightSurface1),
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.cardInnerPadding),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text("Step Trend (last ${uiState.stepTrend.size} days)", style = MaterialTheme.typography.titleMedium)
                    Text(uiState.stepTrend.joinToString(separator = " • "), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun RangeSelector(
    selectedRangeDays: Int,
    onRangeSelected: (Int) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        listOf(7 to "Week", 30 to "Month", 90 to "3M", 365 to "Year").forEach { (days, label) ->
            AssistChip(
                onClick = { onRangeSelected(days) },
                label = { Text(label) },
                modifier = Modifier.testTag("analytics_range_$days"),
            )
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, tag: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag),
        colors = CardDefaults.cardColors(containerColor = LightSurface1),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.cardInnerPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
