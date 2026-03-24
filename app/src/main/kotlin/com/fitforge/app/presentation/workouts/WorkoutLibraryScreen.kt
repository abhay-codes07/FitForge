package com.fitforge.app.presentation.workouts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.fitforge.app.presentation.theme.LightSurface1
import com.fitforge.app.presentation.theme.Purple100
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutLibraryScreen(
    uiState: WorkoutLibraryUiState,
    onQueryChanged: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onDifficultySelected: (String?) -> Unit,
    onExerciseClick: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Workout Library") })
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding)
                .testTag("workout_library_screen"),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("workout_search"),
                label = { Text("Search exercises") },
                singleLine = true,
            )

            FilterChips(
                title = "Category",
                options = uiState.categoryFilters,
                selected = uiState.selectedCategory,
                tagPrefix = "workout_category_",
                onSelected = onCategorySelected,
            )
            FilterChips(
                title = "Difficulty",
                options = uiState.difficultyFilters,
                selected = uiState.selectedDifficulty,
                tagPrefix = "workout_difficulty_",
                onSelected = onDifficultySelected,
            )

            if (uiState.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(modifier = Modifier.testTag("workout_loading"))
                }
                return@Scaffold
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("workout_error"),
                )
            }

            if (uiState.exercises.isEmpty()) {
                Text(
                    text = "No exercises found",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("workout_empty"),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("workout_list"),
                    verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
                ) {
                    items(uiState.exercises, key = { it.id }) { item ->
                        ExerciseCard(item = item, onClick = { onExerciseClick(item.id) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterChips(
    title: String,
    options: List<String>,
    selected: String?,
    tagPrefix: String,
    onSelected: (String?) -> Unit,
) {
    if (options.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                AssistChip(
                    onClick = { onSelected(option) },
                    label = { Text(option) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isSelected) Purple100 else MaterialTheme.colorScheme.surface,
                        labelColor = if (isSelected) Purple600 else MaterialTheme.colorScheme.onSurface,
                    ),
                    modifier = Modifier.testTag("$tagPrefix$option"),
                )
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    item: WorkoutLibraryExerciseItem,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("workout_item_${item.id}"),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = LightSurface1),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.cardInnerPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(item.name, style = MaterialTheme.typography.titleMedium)
            Text("${item.durationLabel} • ${item.caloriesLabel}", style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(item.category, style = MaterialTheme.typography.labelMedium, color = Purple600)
                Text(item.difficulty, style = MaterialTheme.typography.labelMedium)
            }
            if (item.equipment.isNotEmpty()) {
                Text(
                    text = "Equipment: ${item.equipment.joinToString()}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
