package com.fitforge.app.presentation.challenges

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.fitforge.app.data.local.db.entity.ChallengeEntity
import com.fitforge.app.presentation.theme.Spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    uiState: ChallengesUiState,
    onTabSelected: (ChallengeTab) -> Unit,
    onJoinChallenge: (String) -> Unit,
    onLeaveChallenge: (String) -> Unit,
    onDeleteChallenge: (String) -> Unit,
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
        topBar = { TopAppBar(title = { Text("Challenges") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("challenges_loading"))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("challenges_screen"),
        ) {
            // Tabs
            TabRow(selectedTabIndex = uiState.selectedTab.ordinal) {
                Tab(
                    selected = uiState.selectedTab == ChallengeTab.ACTIVE,
                    onClick = { onTabSelected(ChallengeTab.ACTIVE) },
                    text = { Text("Active") },
                    modifier = Modifier.testTag("tab_active"),
                )
                Tab(
                    selected = uiState.selectedTab == ChallengeTab.MY_CHALLENGES,
                    onClick = { onTabSelected(ChallengeTab.MY_CHALLENGES) },
                    text = { Text("My Challenges") },
                    modifier = Modifier.testTag("tab_my_challenges"),
                )
            }

            // Content
            val challenges = when (uiState.selectedTab) {
                ChallengeTab.ACTIVE -> uiState.activeChallenges
                ChallengeTab.MY_CHALLENGES -> uiState.myChallenges
            }

            if (challenges.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No challenges available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    items(challenges, key = { it.id }) { challenge ->
                        ChallengeCard(
                            challenge = challenge,
                            showActions = uiState.selectedTab == ChallengeTab.ACTIVE,
                            showDelete = uiState.selectedTab == ChallengeTab.MY_CHALLENGES,
                            onJoin = { onJoinChallenge(challenge.id) },
                            onLeave = { onLeaveChallenge(challenge.id) },
                            onDelete = { onDeleteChallenge(challenge.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChallengeCard(
    challenge: ChallengeEntity,
    showActions: Boolean,
    showDelete: Boolean,
    onJoin: () -> Unit,
    onLeave: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("challenge_card_${challenge.id}"),
        colors = CardDefaults.cardColors(
            containerColor = when (challenge.status) {
                "active" -> MaterialTheme.colorScheme.primaryContainer
                "completed" -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = challenge.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Challenge details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "Goal: ${challenge.goalValue} ${challenge.unit}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "Type: ${challenge.challengeType}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "Participants: ${challenge.participantIds.size}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                    Text(
                        text = "Start: ${dateFormat.format(Date(challenge.startAtEpochMillis))}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "End: ${dateFormat.format(Date(challenge.endAtEpochMillis))}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (challenge.status == "completed" && challenge.winnerUserId != null) {
                        Text(
                            text = "Winner: ${challenge.winnerUserId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            // Action buttons
            if (showActions) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Button(
                        onClick = onJoin,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("join_button_${challenge.id}"),
                    ) {
                        Text("Join")
                    }
                }
            }

            if (showDelete) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    OutlinedButton(
                        onClick = onLeave,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("leave_button_${challenge.id}"),
                    ) {
                        Text("Leave")
                    }
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("delete_button_${challenge.id}"),
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
