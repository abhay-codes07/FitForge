package com.fitforge.app.presentation.workouthistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.workouts.GetWorkoutHistoryUseCase
import com.fitforge.app.domain.usecase.workouts.GetWorkoutStatisticsUseCase
import com.fitforge.app.domain.usecase.workouts.GetWorkoutStreakUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WorkoutHistoryViewModel @Inject constructor(
    private val getWorkoutHistoryUseCase: GetWorkoutHistoryUseCase,
    private val getWorkoutStreakUseCase: GetWorkoutStreakUseCase,
    private val getWorkoutStatisticsUseCase: GetWorkoutStatisticsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutHistoryUiState())
    val uiState: StateFlow<WorkoutHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun refresh() {
        loadHistory()
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Load workout history
            launch {
                getWorkoutHistoryUseCase.getCompletedWorkouts()
                    .catch { throwable ->
                        _uiState.update {
                            it.copy(errorMessage = throwable.message ?: "Failed to load history")
                        }
                    }
                    .collect { workouts ->
                        _uiState.update {
                            it.copy(
                                workouts = workouts.sortedByDescending { w -> w.completedAtEpochMillis },
                            )
                        }
                    }
            }

            // Load streak
            launch {
                getWorkoutStreakUseCase()
                    .onSuccess { streak ->
                        _uiState.update { it.copy(currentStreak = streak) }
                    }
            }

            // Load statistics
            launch {
                getWorkoutStatisticsUseCase()
                    .onSuccess { stats ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                totalWorkouts = stats.totalWorkouts,
                                totalMinutes = stats.totalMinutes,
                                averageDuration = stats.averageDuration,
                                favoriteType = stats.favoriteWorkoutType,
                            )
                        }
                    }
                    .onFailure { throwable ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = throwable.message ?: "Failed to load statistics",
                            )
                        }
                    }
            }
        }
    }
}
