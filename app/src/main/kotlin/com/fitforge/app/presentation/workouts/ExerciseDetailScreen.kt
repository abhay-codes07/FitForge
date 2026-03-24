package com.fitforge.app.presentation.workouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(uiState: ExerciseDetailUiState) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Exercise Detail") })
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
                CircularProgressIndicator(modifier = Modifier.testTag("exercise_detail_loading"))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding)
                .testTag("exercise_detail_screen"),
            verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
        ) {
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("exercise_detail_error"),
                )
                return@Column
            }

            Text(uiState.title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.testTag("exercise_detail_title"))
            Text(uiState.description, style = MaterialTheme.typography.bodyLarge)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LightSurface1),
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.cardInnerPadding),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text("Category: ${uiState.category}", style = MaterialTheme.typography.bodyMedium)
                    Text("Difficulty: ${uiState.difficulty}", style = MaterialTheme.typography.bodyMedium, color = Purple600)
                    Text("Duration: ${uiState.durationLabel}", style = MaterialTheme.typography.bodyMedium)
                    Text("Calories: ${uiState.caloriesLabel}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Primary Muscles: ${uiState.primaryMuscles.joinToString()}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    if (uiState.equipment.isNotEmpty()) {
                        Text("Equipment: ${uiState.equipment.joinToString()}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exercise_detail_instructions"),
                colors = CardDefaults.cardColors(containerColor = LightSurface1),
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.cardInnerPadding),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text("Instructions", style = MaterialTheme.typography.titleMedium)
                    uiState.instructions.forEachIndexed { index, step ->
                        Text("${index + 1}. $step", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
