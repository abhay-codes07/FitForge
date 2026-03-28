package com.fitforge.app.presentation.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.achievements.ClaimAchievementUseCase
import com.fitforge.app.domain.usecase.achievements.GetAchievementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val getAchievementsUseCase: GetAchievementsUseCase,
    private val claimAchievementUseCase: ClaimAchievementUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        loadAchievements("all")
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadAchievements(category)
    }

    fun onClaimAchievement(achievementId: String) {
        viewModelScope.launch {
            val achievement = _uiState.value.achievements.find { it.id == achievementId }
                ?: return@launch

            claimAchievementUseCase(achievement)
                .onSuccess {
                    // Reload to reflect claimed status
                    loadAchievements(_uiState.value.selectedCategory)
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Failed to claim achievement")
                    }
                }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadAchievements(category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val flow = if (category == "all") {
                getAchievementsUseCase()
            } else {
                getAchievementsUseCase(category)
            }

            flow.catch { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load achievements",
                    )
                }
            }.collect { achievements ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    achievements = achievements.sortedBy { it.unlockedAtEpochMillis == null },
                    errorMessage = null,
                )
            }
        }
    }
}
