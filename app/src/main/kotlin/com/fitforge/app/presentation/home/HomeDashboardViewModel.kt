package com.fitforge.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.home.GetHomeDashboardDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeDashboardViewModel @Inject constructor(
    private val getHomeDashboardDataUseCase: GetHomeDashboardDataUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeDashboardUiState())
    val uiState: StateFlow<HomeDashboardUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { getHomeDashboardDataUseCase() }
                .onSuccess { data ->
                    _uiState.value = HomeDashboardUiState(
                        isLoading = false,
                        isGuest = data.isGuest,
                        greetingName = data.greetingName,
                        steps = data.steps,
                        activeCalories = data.activeCalories,
                        workoutMinutes = data.workoutMinutes,
                        waterIntakeMl = data.waterIntakeMl,
                        latestWeightLabel = data.latestWeightKg?.let { "${"%.1f".format(it)} kg" } ?: "-",
                        nextWorkoutTitle = data.nextWorkoutTitle ?: "No workout scheduled",
                        recentWorkoutTitles = data.recentWorkoutTitles,
                    )
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unable to load dashboard",
                        )
                    }
                }
        }
    }
}
