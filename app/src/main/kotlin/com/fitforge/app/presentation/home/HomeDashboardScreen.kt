package com.fitforge.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.fitforge.app.presentation.theme.LightSurface1
import com.fitforge.app.presentation.theme.Mint500
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Radius
import com.fitforge.app.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(
    uiState: HomeDashboardUiState,
    onRefresh: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Dashboard") },
            )
        },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("home_loading"))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding)
                .testTag("home_dashboard_screen"),
            verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
        ) {
            Text(
                text = "Welcome back, ${uiState.greetingName}",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("home_greeting"),
            )
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("home_error"),
                )
            }

            StatsRow(
                title = "Steps",
                value = uiState.steps.toString(),
                tag = "home_steps_value",
                accent = Purple600,
            )
            StatsRow(
                title = "Active Calories",
                value = "${uiState.activeCalories} kcal",
                tag = "home_calories_value",
                accent = Mint500,
            )
            StatsRow(
                title = "Workout Minutes",
                value = "${uiState.workoutMinutes} min",
                tag = "home_workout_minutes_value",
                accent = Purple600,
            )
            StatsRow(
                title = "Water Intake",
                value = "${uiState.waterIntakeMl} ml",
                tag = "home_water_value",
                accent = Mint500,
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_next_workout"),
                shape = RoundedCornerShape(Radius.md),
                colors = CardDefaults.cardColors(containerColor = LightSurface1),
            ) {
                Column(modifier = Modifier.padding(Spacing.cardInnerPadding)) {
                    Text("Next Workout", style = MaterialTheme.typography.titleMedium)
                    Text(uiState.nextWorkoutTitle, style = MaterialTheme.typography.bodyLarge)
                    Text("Latest Weight: ${uiState.latestWeightLabel}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_recent_workouts"),
                shape = RoundedCornerShape(Radius.md),
                colors = CardDefaults.cardColors(containerColor = LightSurface1),
            ) {
                Column(modifier = Modifier.padding(Spacing.cardInnerPadding)) {
                    Text("Recent Workouts", style = MaterialTheme.typography.titleMedium)
                    if (uiState.recentWorkoutTitles.isEmpty()) {
                        Text("No recent workouts yet", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        uiState.recentWorkoutTitles.forEachIndexed { index, title ->
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.testTag("home_recent_workout_$index"),
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onRefresh,
                modifier = Modifier
                    .width(160.dp)
                    .align(Alignment.CenterHorizontally)
                    .testTag("home_refresh"),
            ) {
                Text("Refresh")
            }
        }
    }
}

@Composable
private fun StatsRow(
    title: String,
    value: String,
    tag: String,
    accent: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightSurface1, RoundedCornerShape(Radius.md))
            .padding(Spacing.cardInnerPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = accent,
            modifier = Modifier.testTag(tag),
        )
    }
}
