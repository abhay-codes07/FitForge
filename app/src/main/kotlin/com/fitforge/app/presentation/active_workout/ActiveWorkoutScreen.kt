package com.fitforge.app.presentation.active_workout

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fitforge.app.presentation.theme.DarkSurface1
import com.fitforge.app.presentation.theme.DarkSurface3
import com.fitforge.app.presentation.theme.FitForgeCustomType
import com.fitforge.app.presentation.theme.LightSurface0
import com.fitforge.app.presentation.theme.Mint500
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Radius
import com.fitforge.app.presentation.theme.Spacing
import com.fitforge.app.service.WorkoutTimerService

@Composable
fun ActiveWorkoutScreen(
    uiState: ActiveWorkoutUiState,
    onClose: () -> Unit,
    onShuffle: () -> Unit,
    onSkip: () -> Unit,
    onRepIncrement: () -> Unit,
    onRepDecrement: () -> Unit,
    onCompleteSet: () -> Unit,
    onDismissRestTimer: () -> Unit,
    onRetry: () -> Unit,
    onDismissError: () -> Unit,
    timerServiceEnabled: Boolean = true,
) {
    val context = LocalContext.current

    if (timerServiceEnabled) {
        LaunchedEffect(uiState.restSecondsRemaining > 0) {
            if (uiState.restSecondsRemaining > 0) {
                WorkoutTimerService.start(context, uiState.restSecondsRemaining)
            } else {
                WorkoutTimerService.stop(context)
            }
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("active_workout_loading"),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("active_workout_screen"),
    ) {
        AsyncImage(
            model = uiState.exerciseImageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x99000000), Color.Transparent, Color(0xCC000000)),
                    ),
                ),
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .padding(Spacing.md)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x66000000))
                .align(Alignment.TopStart)
                .testTag("active_workout_close"),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close workout",
                tint = Color.White,
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = Radius.xl, topEnd = Radius.xl),
            color = if (MaterialTheme.colorScheme.background == LightSurface0) LightSurface0 else DarkSurface1,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl, vertical = Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(
                    text = uiState.exerciseName,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.testTag("active_workout_exercise_name"),
                )

                Text(
                    text = uiState.repCount.toString(),
                    style = FitForgeCustomType.timerDisplay,
                    color = Mint500,
                    modifier = Modifier.testTag("active_workout_rep_counter"),
                )

                Text(
                    text = "Target ${uiState.targetReps} reps • Set ${uiState.currentSet}/${uiState.targetSets}",
                    style = MaterialTheme.typography.bodyMedium,
                )

                if (uiState.restSecondsRemaining > 0) {
                    Button(
                        onClick = onDismissRestTimer,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface3),
                        modifier = Modifier.testTag("active_workout_rest_timer"),
                    ) {
                        Text("Rest ${uiState.restSecondsRemaining}s")
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Button(onClick = onRepDecrement, modifier = Modifier.testTag("active_workout_rep_decrement")) {
                        Text("-")
                    }
                    Button(onClick = onRepIncrement, modifier = Modifier.testTag("active_workout_rep_increment")) {
                        Text("+")
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Button(onClick = onShuffle, modifier = Modifier.testTag("active_workout_shuffle")) {
                        Icon(imageVector = Icons.Default.Shuffle, contentDescription = null)
                        Spacer(Modifier.size(Spacing.xs))
                        Text("Shuffle")
                    }
                    Button(onClick = onSkip, modifier = Modifier.testTag("active_workout_skip")) {
                        Icon(imageVector = Icons.Default.SkipNext, contentDescription = null)
                        Spacer(Modifier.size(Spacing.xs))
                        Text("Skip")
                    }
                }

                Button(
                    onClick = onCompleteSet,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_workout_complete_set"),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                ) {
                    Text("Complete Set", fontWeight = FontWeight.SemiBold)
                }

                WorkoutProgressBar(
                    currentIndex = uiState.currentExerciseIndex,
                    total = uiState.totalExercises,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .testTag("active_workout_progress"),
                )

                if (uiState.errorMessage != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onDismissError) { Text("Dismiss") }
                        Button(onClick = onRetry) { Text("Retry") }
                    }
                }

                if (uiState.isWorkoutCompleted) {
                    Text(
                        text = "Workout complete",
                        style = MaterialTheme.typography.titleMedium,
                        color = Mint500,
                        modifier = Modifier.testTag("active_workout_complete"),
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutProgressBar(
    currentIndex: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    if (total <= 0) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        repeat(total) { index ->
            val color = when {
                index < currentIndex -> Mint500
                index == currentIndex -> Purple600
                else -> DarkSurface3
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(color),
            )
        }
    }
}
