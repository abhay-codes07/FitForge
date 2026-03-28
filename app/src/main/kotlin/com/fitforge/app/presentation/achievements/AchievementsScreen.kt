package com.fitforge.app.presentation.achievements

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
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
import com.fitforge.app.data.local.db.entity.AchievementEntity
import com.fitforge.app.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    uiState: AchievementsUiState,
    onCategorySelected: (String) -> Unit,
    onClaimAchievement: (String) -> Unit,
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
        topBar = { TopAppBar(title = { Text("Achievements") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("achievements_loading"))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding)
                .testTag("achievements_screen"),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            // Category filter
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                items(uiState.categories) { category ->
                    AssistChip(
                        onClick = { onCategorySelected(category) },
                        label = { Text(category.replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier.testTag("category_chip_$category"),
                    )
                }
            }

            // Achievement stats
            val unlocked = uiState.achievements.count { it.unlockedAtEpochMillis != null }
            val total = uiState.achievements.size
            Text(
                text = "Unlocked: $unlocked / $total",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("achievements_stats"),
            )

            // Achievements list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(uiState.achievements, key = { it.id }) { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        onClaim = { onClaimAchievement(achievement.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: AchievementEntity,
    onClaim: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("achievement_card_${achievement.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.unlockedAtEpochMillis != null) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                Column {
                    LinearProgressIndicator(
                        progress = (achievement.progress / achievement.target).coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${achievement.progress.toInt()} / ${achievement.target.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.sm))

            // Action button
            if (achievement.unlockedAtEpochMillis != null && !achievement.isClaimed) {
                Button(
                    onClick = onClaim,
                    modifier = Modifier.testTag("claim_button_${achievement.id}"),
                ) {
                    Text("Claim")
                }
            } else if (achievement.isClaimed) {
                Text(
                    text = "Claimed",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
