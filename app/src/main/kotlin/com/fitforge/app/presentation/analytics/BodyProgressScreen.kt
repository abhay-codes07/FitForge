package com.fitforge.app.presentation.analytics

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
import androidx.compose.ui.text.input.KeyboardType
import com.fitforge.app.presentation.theme.LightSurface1
import com.fitforge.app.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyProgressScreen(
    uiState: BodyProgressUiState,
    onRangeSelected: (Int) -> Unit,
    onWeightChanged: (String) -> Unit,
    onBodyFatChanged: (String) -> Unit,
    onPhotoUriChanged: (String) -> Unit,
    onNoteChanged: (String) -> Unit,
    onSaveMeasurement: () -> Unit,
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Body Progress") }) }) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("body_progress_loading"))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding)
                .testTag("body_progress_screen"),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    listOf(30, 90, 180, 365).forEach { days ->
                        AssistChip(
                            onClick = { onRangeSelected(days) },
                            label = { Text("${days}d") },
                            modifier = Modifier.testTag("body_progress_range_$days"),
                        )
                    }
                }
            }

            item { StatCard("Latest Weight", uiState.latestWeightLabel, "body_progress_latest_weight") }
            item { StatCard("Latest Body Fat", uiState.latestBodyFatLabel, "body_progress_latest_bodyfat") }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("body_progress_trend"),
                    colors = CardDefaults.cardColors(containerColor = LightSurface1),
                ) {
                    Column(modifier = Modifier.padding(Spacing.cardInnerPadding), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Text("Weight Trend", style = MaterialTheme.typography.titleSmall)
                        Text(uiState.weightTrend.joinToString(" • "), style = MaterialTheme.typography.bodySmall)
                        Text("Body Fat Trend", style = MaterialTheme.typography.titleSmall)
                        Text(uiState.bodyFatTrend.joinToString(" • "), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = uiState.weightInput,
                    onValueChange = onWeightChanged,
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("body_progress_weight_input"),
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.bodyFatInput,
                    onValueChange = onBodyFatChanged,
                    label = { Text("Body Fat (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("body_progress_bodyfat_input"),
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.photoUriInput,
                    onValueChange = onPhotoUriChanged,
                    label = { Text("Progress Photo URI") },
                    modifier = Modifier.fillMaxWidth().testTag("body_progress_photo_input"),
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.noteInput,
                    onValueChange = onNoteChanged,
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth().testTag("body_progress_note_input"),
                )
            }
            item {
                Button(
                    onClick = onSaveMeasurement,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth().testTag("body_progress_save"),
                ) {
                    Text(if (uiState.isSaving) "Saving..." else "Save Measurement")
                }
            }

            if (uiState.errorMessage != null) {
                item { Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("body_progress_error")) }
            }
            if (uiState.successMessage != null) {
                item { Text(uiState.successMessage, modifier = Modifier.testTag("body_progress_success")) }
            }

            items(uiState.entries, key = { it.id }) { entry ->
                Card(modifier = Modifier.fillMaxWidth().testTag("body_progress_entry_${entry.id}"), colors = CardDefaults.cardColors(containerColor = LightSurface1)) {
                    Column(modifier = Modifier.padding(Spacing.cardInnerPadding), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Text(entry.recordedAtLabel, style = MaterialTheme.typography.titleSmall)
                        Text("Weight: ${entry.weightLabel}", style = MaterialTheme.typography.bodyMedium)
                        Text("Body Fat: ${entry.bodyFatLabel}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, tag: String) {
    Card(modifier = Modifier.fillMaxWidth().testTag(tag), colors = CardDefaults.cardColors(containerColor = LightSurface1)) {
        Column(modifier = Modifier.padding(Spacing.cardInnerPadding), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
