package com.fitforge.app.presentation.active_workout

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.fitforge.app.presentation.theme.Mint500
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Radius
import com.fitforge.app.presentation.theme.Spacing

@Composable
fun WorkoutSummaryScreen(
    uiState: WorkoutSummaryUiState,
    onDoneClick: () -> Unit,
    onRetry: () -> Unit,
    onDismissError: () -> Unit,
) {
    if (uiState.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("workout_summary_loading"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Spacing.md)
            .testTag("workout_summary_screen"),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = "Workout Complete",
            style = MaterialTheme.typography.headlineMedium,
            color = Mint500,
        )
        Text(
            text = uiState.workoutName,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = uiState.completedAtLabel,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.testTag("workout_summary_completed_at"),
        )

        SummaryStatRow(
            firstLabel = "Duration",
            firstValue = uiState.durationLabel,
            secondLabel = "Calories",
            secondValue = uiState.caloriesLabel,
        )
        SummaryStatRow(
            firstLabel = "Sets",
            firstValue = uiState.totalSetsLabel,
            secondLabel = "Reps",
            secondValue = uiState.totalRepsLabel,
        )
        SummaryStatRow(
            firstLabel = "Volume",
            firstValue = uiState.totalVolumeLabel,
            secondLabel = "PRs",
            secondValue = uiState.personalRecordsLabel,
        )

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag("workout_summary_error"),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Button(onClick = onDismissError) { Text("Dismiss") }
                Button(onClick = onRetry) { Text("Retry") }
            }
        }

        Button(
            onClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, uiState.shareText)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share workout summary"))
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("workout_summary_share"),
            colors = ButtonDefaults.buttonColors(containerColor = Purple600),
        ) {
            Text("Share")
        }

        Button(
            onClick = onDoneClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("workout_summary_done"),
        ) {
            Text("Done")
        }
    }
}

@Composable
private fun SummaryStatRow(
    firstLabel: String,
    firstValue: String,
    secondLabel: String,
    secondValue: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        SummaryStatCard(
            modifier = Modifier.weight(1f),
            label = firstLabel,
            value = firstValue,
        )
        SummaryStatCard(
            modifier = Modifier.weight(1f),
            label = secondLabel,
            value = secondValue,
        )
    }
}

@Composable
private fun SummaryStatCard(
    modifier: Modifier,
    label: String,
    value: String,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Radius.md),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            Text(text = value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

