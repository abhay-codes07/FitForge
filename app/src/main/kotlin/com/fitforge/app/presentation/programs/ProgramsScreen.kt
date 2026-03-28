package com.fitforge.app.presentation.programs

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.fitforge.app.data.local.db.entity.ProgramEntity
import com.fitforge.app.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramsScreen(
    uiState: ProgramsUiState,
    onGoalSelected: (String) -> Unit,
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
        topBar = { TopAppBar(title = { Text("Training Programs") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("programs_loading"))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding)
                .testTag("programs_screen"),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            // Goal filter
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                items(uiState.goals) { goal ->
                    AssistChip(
                        onClick = { onGoalSelected(goal) },
                        label = { Text(goal.replace("_", " ").replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier.testTag("goal_chip_$goal"),
                    )
                }
            }

            // Programs list
            if (uiState.programs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No programs available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    items(uiState.programs, key = { it.id }) { program ->
                        ProgramCard(program = program)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgramCard(program: ProgramEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("program_card_${program.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (program.isPremium) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            },
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = program.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                if (program.isPremium) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Premium",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Text(
                text = program.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Program details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "Goal: ${program.goal.replace("_", " ")}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "Duration: ${program.durationWeeks} weeks",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "Difficulty: ${program.difficulty}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${program.workoutIds.size} workouts",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
