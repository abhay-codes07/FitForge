package com.fitforge.app.presentation.active_workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
fun StartWorkoutScreen(
    uiState: StartWorkoutUiState,
    onOptionSelected: (String) -> Unit,
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Start Workout") }) }) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("start_workout_loading"))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("start_workout_screen"),
        ) {
            ModalBottomSheet(
                onDismissRequest = { },
                dragHandle = null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Text(
                        text = "Quick Start",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = uiState.scheduledWorkoutCountLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("start_workout_scheduled_count"),
                    )

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.testTag("start_workout_error"),
                        )
                    }

                    uiState.options.forEach { option ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_option_$option"),
                            colors = CardDefaults.cardColors(containerColor = LightSurface1),
                        ) {
                            Button(
                                onClick = { onOptionSelected(option) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.xs),
                            ) {
                                Text(option)
                            }
                        }
                    }
                }
            }
        }
    }
}
