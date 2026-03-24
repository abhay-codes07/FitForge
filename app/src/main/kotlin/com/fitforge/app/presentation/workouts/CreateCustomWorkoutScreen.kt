package com.fitforge.app.presentation.workouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import com.fitforge.app.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomWorkoutScreen(
    uiState: CreateCustomWorkoutUiState,
    onNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onWorkoutTypeSelected: (String) -> Unit,
    onDifficultySelected: (String) -> Unit,
    onDurationChanged: (String) -> Unit,
    onExerciseToggled: (String) -> Unit,
    onSaveClick: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Create Custom Workout") }) },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("custom_workout_loading"))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding)
                .testTag("custom_workout_screen"),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            item {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChanged,
                    label = { Text("Workout Name") },
                    modifier = Modifier.fillMaxWidth().testTag("custom_workout_name"),
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChanged,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().testTag("custom_workout_description"),
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.durationMinutes,
                    onValueChange = onDurationChanged,
                    label = { Text("Duration (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("custom_workout_duration"),
                )
            }
            item {
                FilterRow(
                    title = "Type",
                    options = uiState.workoutTypeOptions,
                    selected = uiState.selectedWorkoutType,
                    tagPrefix = "custom_workout_type_",
                    onSelected = onWorkoutTypeSelected,
                )
            }
            item {
                FilterRow(
                    title = "Difficulty",
                    options = uiState.difficultyOptions,
                    selected = uiState.selectedDifficulty,
                    tagPrefix = "custom_workout_diff_",
                    onSelected = onDifficultySelected,
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChanged,
                    label = { Text("Search Exercises") },
                    modifier = Modifier.fillMaxWidth().testTag("custom_workout_search"),
                )
            }
            items(uiState.exercises, key = { it.id }) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_exercise_${item.id}"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, style = MaterialTheme.typography.titleSmall)
                        Text(item.category, style = MaterialTheme.typography.bodySmall)
                    }
                    Checkbox(
                        checked = uiState.selectedExerciseIds.contains(item.id),
                        onCheckedChange = { onExerciseToggled(item.id) },
                    )
                }
            }
            if (uiState.errorMessage != null) {
                item { Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("custom_workout_error")) }
            }
            if (uiState.successMessage != null) {
                item { Text(uiState.successMessage, modifier = Modifier.testTag("custom_workout_success")) }
            }
            item {
                Button(
                    onClick = onSaveClick,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth().testTag("custom_workout_save"),
                ) {
                    Text(if (uiState.isSaving) "Saving..." else "Create Workout")
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    title: String,
    options: List<String>,
    selected: String,
    tagPrefix: String,
    onSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            options.forEach { option ->
                AssistChip(
                    onClick = { onSelected(option) },
                    label = { Text(option) },
                    modifier = Modifier.testTag("$tagPrefix$option"),
                )
            }
        }
    }
}
