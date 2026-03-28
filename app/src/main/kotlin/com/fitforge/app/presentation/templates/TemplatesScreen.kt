package com.fitforge.app.presentation.templates

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.fitforge.app.data.local.db.entity.WorkoutTemplateEntity
import com.fitforge.app.presentation.theme.Spacing

data class TemplatesUiState(
    val isLoading: Boolean = true,
    val templates: List<WorkoutTemplateEntity> = emptyList(),
    val publicTemplates: List<WorkoutTemplateEntity> = emptyList(),
    val errorMessage: String? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    uiState: TemplatesUiState,
    onUseTemplate: (String) -> Unit,
    onDeleteTemplate: (String) -> Unit,
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
        topBar = { TopAppBar(title = { Text("Workout Templates") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("templates_loading"))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding)
                .testTag("templates_screen"),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            // My Templates Section
            item {
                Text(
                    text = "My Templates",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (uiState.templates.isEmpty()) {
                item {
                    Text(
                        text = "No saved templates yet. Complete a workout and save it as a template!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(uiState.templates, key = { it.id }) { template ->
                    TemplateCard(
                        template = template,
                        onUse = { onUseTemplate(template.id) },
                        onDelete = { onDeleteTemplate(template.id) },
                        showDelete = true,
                    )
                }
            }

            // Public Templates Section
            if (uiState.publicTemplates.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text(
                        text = "Community Templates",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }

                items(uiState.publicTemplates, key = { it.id }) { template ->
                    TemplateCard(
                        template = template,
                        onUse = { onUseTemplate(template.id) },
                        onDelete = {},
                        showDelete = false,
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: WorkoutTemplateEntity,
    onUse: () -> Unit,
    onDelete: () -> Unit,
    showDelete: Boolean,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("template_card_${template.id}"),
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
                        text = template.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${template.workoutType.replaceFirstChar { it.uppercase() }} • ${template.difficulty}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (showDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            template.description?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(
                    text = "⏱️ ${template.estimatedDurationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                )
                template.estimatedCalories?.let { calories ->
                    Text(
                        text = "🔥 $calories kcal",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text(
                    text = "📊 Used ${template.useCount}x",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            Button(
                onClick = onUse,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Text("Use Template")
            }
        }
    }
}
