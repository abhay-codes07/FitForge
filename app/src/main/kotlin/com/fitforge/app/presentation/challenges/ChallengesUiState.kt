package com.fitforge.app.presentation.challenges

import com.fitforge.app.data.local.db.entity.ChallengeEntity

data class ChallengesUiState(
    val isLoading: Boolean = true,
    val activeChallenges: List<ChallengeEntity> = emptyList(),
    val myChallenges: List<ChallengeEntity> = emptyList(),
    val selectedTab: ChallengeTab = ChallengeTab.ACTIVE,
    val errorMessage: String? = null,
)

enum class ChallengeTab {
    ACTIVE,
    MY_CHALLENGES,
}
