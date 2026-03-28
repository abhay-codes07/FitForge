package com.fitforge.app.presentation.social

import com.fitforge.app.data.local.db.entity.ActivityCommentEntity
import com.fitforge.app.data.local.db.entity.ActivityFeedItemEntity
import com.fitforge.app.data.local.db.entity.FriendEntity

data class SocialUiState(
    val isLoading: Boolean = true,
    val friends: List<FriendEntity> = emptyList(),
    val pendingRequests: List<FriendEntity> = emptyList(),
    val friendCount: Int = 0,
    val pendingRequestCount: Int = 0,
    val activityFeed: List<ActivityFeedItemEntity> = emptyList(),
    val selectedTab: SocialTab = SocialTab.FEED,
    val errorMessage: String? = null,
)

enum class SocialTab {
    FEED,
    FRIENDS,
}

data class ActivityDetailUiState(
    val activity: ActivityFeedItemEntity? = null,
    val comments: List<ActivityCommentEntity> = emptyList(),
    val isLoading: Boolean = true,
    val commentText: String = "",
    val errorMessage: String? = null,
)
