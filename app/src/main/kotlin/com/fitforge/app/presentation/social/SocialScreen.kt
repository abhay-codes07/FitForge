package com.fitforge.app.presentation.social

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitforge.app.data.local.db.entity.ActivityFeedItemEntity
import com.fitforge.app.data.local.db.entity.FriendEntity
import com.fitforge.app.presentation.theme.Spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(
    uiState: SocialUiState,
    onTabSelected: (SocialTab) -> Unit,
    onSendFriendRequest: (String) -> Unit,
    onAcceptRequest: (String) -> Unit,
    onDeclineRequest: (String) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onToggleLike: (String, Boolean) -> Unit,
    onRefresh: () -> Unit,
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
        topBar = {
            TopAppBar(
                title = { Text("Social") },
                actions = {
                    IconButton(onClick = { /* TODO: Open add friend dialog */ }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Friend")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            TabRow(selectedTabIndex = uiState.selectedTab.ordinal) {
                Tab(
                    selected = uiState.selectedTab == SocialTab.FEED,
                    onClick = { onTabSelected(SocialTab.FEED) },
                    text = { Text("Activity Feed") },
                )
                Tab(
                    selected = uiState.selectedTab == SocialTab.FRIENDS,
                    onClick = { onTabSelected(SocialTab.FRIENDS) },
                    text = {
                        BadgedBox(
                            badge = {
                                if (uiState.pendingRequestCount > 0) {
                                    Badge { Text(uiState.pendingRequestCount.toString()) }
                                }
                            },
                        ) {
                            Text("Friends (${uiState.friendCount})")
                        }
                    },
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.testTag("social_loading"))
                }
                return@Column
            }

            when (uiState.selectedTab) {
                SocialTab.FEED -> ActivityFeedTab(
                    activities = uiState.activityFeed,
                    onToggleLike = onToggleLike,
                )
                SocialTab.FRIENDS -> FriendsTab(
                    friends = uiState.friends,
                    pendingRequests = uiState.pendingRequests,
                    onAcceptRequest = onAcceptRequest,
                    onDeclineRequest = onDeclineRequest,
                    onRemoveFriend = onRemoveFriend,
                )
            }
        }
    }
}

@Composable
private fun ActivityFeedTab(
    activities: List<ActivityFeedItemEntity>,
    onToggleLike: (String, Boolean) -> Unit,
) {
    if (activities.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("feed_empty"),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No activity yet. Add friends to see their workouts!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.screenHorizontalPadding)
            .testTag("activity_feed"),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        item { Spacer(modifier = Modifier.height(Spacing.sm)) }
        items(activities, key = { it.id }) { activity ->
            ActivityCard(
                activity = activity,
                onToggleLike = { onToggleLike(activity.id, activity.isLikedByMe) },
            )
        }
    }
}

@Composable
private fun ActivityCard(
    activity: ActivityFeedItemEntity,
    onToggleLike: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("activity_card_${activity.id}"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
        ) {
            // User info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = activity.userName.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.sm))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activity.userName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    val timeFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                    Text(
                        text = timeFormat.format(Date(activity.createdAtEpochMillis)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Activity content
            Text(
                text = activity.activityTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = activity.activityDescription,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onToggleLike),
                ) {
                    Icon(
                        imageVector = if (activity.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (activity.isLikedByMe) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = activity.likeCount.toString(),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text(
                    text = "${activity.commentCount} comments",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun FriendsTab(
    friends: List<FriendEntity>,
    pendingRequests: List<FriendEntity>,
    onAcceptRequest: (String) -> Unit,
    onDeclineRequest: (String) -> Unit,
    onRemoveFriend: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.screenHorizontalPadding)
            .testTag("friends_tab"),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        item { Spacer(modifier = Modifier.height(Spacing.sm)) }

        // Pending Requests
        if (pendingRequests.isNotEmpty()) {
            item {
                Text(
                    text = "Pending Requests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = Spacing.sm),
                )
            }
            items(pendingRequests, key = { it.id }) { request ->
                FriendRequestCard(
                    friend = request,
                    onAccept = { onAcceptRequest(request.id) },
                    onDecline = { onDeclineRequest(request.id) },
                )
            }
        }

        // Friends List
        if (friends.isNotEmpty()) {
            item {
                Text(
                    text = "Friends",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = Spacing.sm),
                )
            }
            items(friends, key = { it.id }) { friend ->
                FriendCard(
                    friend = friend,
                    onRemove = { onRemoveFriend(friend.id) },
                )
            }
        } else if (pendingRequests.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No friends yet. Start adding friends!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun FriendRequestCard(
    friend: FriendEntity,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("friend_request_${friend.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = friend.friendName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Spacer(modifier = Modifier.width(Spacing.sm))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.friendName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = friend.friendEmail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onAccept) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Accept",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = onDecline) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Decline",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun FriendCard(
    friend: FriendEntity,
    onRemove: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("friend_card_${friend.id}"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = friend.friendName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Spacer(modifier = Modifier.width(Spacing.sm))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.friendName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = friend.friendEmail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            OutlinedButton(onClick = onRemove) {
                Text("Remove")
            }
        }
    }
}
