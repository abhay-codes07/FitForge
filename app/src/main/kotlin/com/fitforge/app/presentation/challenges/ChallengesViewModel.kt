package com.fitforge.app.presentation.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.challenges.DeleteChallengeUseCase
import com.fitforge.app.domain.usecase.challenges.GetActiveChallengesUseCase
import com.fitforge.app.domain.usecase.challenges.GetMyChallengesUseCase
import com.fitforge.app.domain.usecase.challenges.JoinChallengeUseCase
import com.fitforge.app.domain.usecase.challenges.LeaveChallengeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val getActiveChallengesUseCase: GetActiveChallengesUseCase,
    private val getMyChallengesUseCase: GetMyChallengesUseCase,
    private val joinChallengeUseCase: JoinChallengeUseCase,
    private val leaveChallengeUseCase: LeaveChallengeUseCase,
    private val deleteChallengeUseCase: DeleteChallengeUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChallengesUiState())
    val uiState: StateFlow<ChallengesUiState> = _uiState.asStateFlow()

    init {
        loadChallenges()
    }

    fun onTabSelected(tab: ChallengeTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onJoinChallenge(challengeId: String) {
        viewModelScope.launch {
            joinChallengeUseCase(challengeId)
                .onSuccess {
                    loadChallenges()
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Failed to join challenge")
                    }
                }
        }
    }

    fun onLeaveChallenge(challengeId: String) {
        viewModelScope.launch {
            leaveChallengeUseCase(challengeId)
                .onSuccess {
                    loadChallenges()
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Failed to leave challenge")
                    }
                }
        }
    }

    fun onDeleteChallenge(challengeId: String) {
        viewModelScope.launch {
            deleteChallengeUseCase(challengeId)
                .onSuccess {
                    loadChallenges()
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Failed to delete challenge")
                    }
                }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadChallenges() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Load active challenges
            launch {
                getActiveChallengesUseCase()
                    .catch { throwable ->
                        _uiState.update {
                            it.copy(errorMessage = throwable.message ?: "Failed to load challenges")
                        }
                    }
                    .collect { challenges ->
                        _uiState.update { it.copy(activeChallenges = challenges) }
                    }
            }

            // Load my challenges
            launch {
                getMyChallengesUseCase()
                    .catch { throwable ->
                        _uiState.update {
                            it.copy(errorMessage = throwable.message ?: "Failed to load your challenges")
                        }
                    }
                    .collect { challenges ->
                        _uiState.update {
                            it.copy(
                                myChallenges = challenges,
                                isLoading = false,
                            )
                        }
                    }
            }
        }
    }
}
