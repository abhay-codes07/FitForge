package com.fitforge.app.presentation.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.social.AcceptFriendRequestUseCase
import com.fitforge.app.domain.usecase.social.DeclineFriendRequestUseCase
import com.fitforge.app.domain.usecase.social.GetActivityFeedUseCase
import com.fitforge.app.domain.usecase.social.GetFriendCountUseCase
import com.fitforge.app.domain.usecase.social.GetFriendsUseCase
import com.fitforge.app.domain.usecase.social.GetPendingFriendRequestsUseCase
import com.fitforge.app.domain.usecase.social.GetPendingRequestCountUseCase
import com.fitforge.app.domain.usecase.social.LikeActivityUseCase
import com.fitforge.app.domain.usecase.social.RemoveFriendUseCase
import com.fitforge.app.domain.usecase.social.SendFriendRequestUseCase
import com.fitforge.app.domain.usecase.social.UnlikeActivityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val getActivityFeedUseCase: GetActivityFeedUseCase,
    private val getFriendsUseCase: GetFriendsUseCase,
    private val getPendingFriendRequestsUseCase: GetPendingFriendRequestsUseCase,
    private val getFriendCountUseCase: GetFriendCountUseCase,
    private val getPendingRequestCountUseCase: GetPendingRequestCountUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val declineFriendRequestUseCase: DeclineFriendRequestUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase,
    private val likeActivityUseCase: LikeActivityUseCase,
    private val unlikeActivityUseCase: UnlikeActivityUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SocialUiState())
    val uiState: StateFlow<SocialUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onTabSelected(tab: SocialTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun refresh() {
        loadData()
    }

    fun sendFriendRequest(email: String) {
        viewModelScope.launch {
            sendFriendRequestUseCase(email)
                .onSuccess {
                    _uiState.update { it.copy(errorMessage = "Friend request sent!") }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Failed to send request")
                    }
                }
        }
    }

    fun acceptFriendRequest(friendshipId: String) {
        viewModelScope.launch {
            acceptFriendRequestUseCase(friendshipId)
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Failed to accept request")
                    }
                }
        }
    }

    fun declineFriendRequest(friendshipId: String) {
        viewModelScope.launch {
            declineFriendRequestUseCase(friendshipId)
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Failed to decline request")
                    }
                }
        }
    }

    fun removeFriend(friendshipId: String) {
        viewModelScope.launch {
            removeFriendUseCase(friendshipId)
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Failed to remove friend")
                    }
                }
        }
    }

    fun toggleLikeActivity(activityId: String, isLiked: Boolean) {
        viewModelScope.launch {
            if (isLiked) {
                unlikeActivityUseCase(activityId)
            } else {
                likeActivityUseCase(activityId)
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Load activity feed
            launch {
                getActivityFeedUseCase()
                    .catch { throwable ->
                        _uiState.update {
                            it.copy(errorMessage = throwable.message ?: "Failed to load feed")
                        }
                    }
                    .collect { activities ->
                        _uiState.update { it.copy(activityFeed = activities) }
                    }
            }

            // Load friends
            launch {
                getFriendsUseCase()
                    .catch { throwable ->
                        _uiState.update {
                            it.copy(errorMessage = throwable.message ?: "Failed to load friends")
                        }
                    }
                    .collect { friends ->
                        _uiState.update { it.copy(friends = friends) }
                    }
            }

            // Load pending requests
            launch {
                getPendingFriendRequestsUseCase()
                    .catch { throwable ->
                        _uiState.update {
                            it.copy(errorMessage = throwable.message ?: "Failed to load requests")
                        }
                    }
                    .collect { requests ->
                        _uiState.update { it.copy(pendingRequests = requests) }
                    }
            }

            // Load friend count
            launch {
                getFriendCountUseCase()
                    .collect { count ->
                        _uiState.update { it.copy(friendCount = count) }
                    }
            }

            // Load pending request count
            launch {
                getPendingRequestCountUseCase()
                    .collect { count ->
                        _uiState.update { it.copy(pendingRequestCount = count, isLoading = false) }
                    }
            }
        }
    }
}
