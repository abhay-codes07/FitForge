package com.fitforge.app.presentation.workouthistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.presentation.theme.Spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutHistoryScreen(
    uiState: WorkoutHistoryUiState,
    onRefresh: () -> Unit,
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
        topBar = { TopAppBar(title = { Text("Workout History") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("history_loading"))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding)
                .testTag("history_screen"),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            // Streak Card
            item {
                StreakCard(
                    streak = uiState.currentStreak,
                    totalWorkouts = uiState.totalWorkouts,
                )
            }

            // Statistics Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    StatCard(
                        title = "Total Time",
                        value = "${uiState.totalMinutes / 60}h ${uiState.totalMinutes % 60}m",
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        title = "Avg Duration",
                        value = "${uiState.averageDuration} min",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (uiState.favoriteType.isNotEmpty()) {
                item {
                    StatCard(
                        title = "Favorite Type",
                        value = uiState.favoriteType.replaceFirstChar { it.uppercase() },
                    )
                }
            }

            // Workout History Header
            item {
                Text(
                    text = "Recent Workouts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = Spacing.sm),
                )
            }

            // Workout List
            if (uiState.workouts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No completed workouts yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                items(uiState.workouts, key = { it.id }) { workout ->
                    WorkoutHistoryCard(workout = workout)
                }
            }
        }
    }
}

@Composable
private fun StreakCard(
    streak: Int,
    totalWorkouts: Int,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("streak_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "🔥",
                style = MaterialTheme.typography.displayLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$streak Day Streak",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$totalWorkouts workouts completed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .testTag("stat_card_$title"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
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

@Composable
private fun WorkoutHistoryCard(workout: WorkoutEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("workout_card_${workout.id}"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = workout.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = workout.workoutType.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    workout.completedAtEpochMillis?.let { millis ->
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        Text(
                            text = dateFormat.format(Date(millis)),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                        Text(
                            text = timeFormat.format(Date(millis)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(
                    text = "⏱️ ${workout.estimatedDurationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                )
                workout.estimatedCalories?.let { calories ->
                    Text(
                        text = "🔥 $calories kcal",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text(
                    text = "💪 ${workout.difficulty}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
