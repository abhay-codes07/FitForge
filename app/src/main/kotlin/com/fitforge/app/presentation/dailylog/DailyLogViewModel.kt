package com.fitforge.app.presentation.dailylog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.dailylog.AddWaterIntakeUseCase
import com.fitforge.app.domain.usecase.dailylog.GetTodayLogUseCase
import com.fitforge.app.domain.usecase.dailylog.UpdateSleepUseCase
import com.fitforge.app.domain.usecase.dailylog.UpdateStepsUseCase
import com.fitforge.app.domain.usecase.dailylog.UpdateWaterIntakeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DailyLogViewModel @Inject constructor(
    private val getTodayLogUseCase: GetTodayLogUseCase,
    private val addWaterIntakeUseCase: AddWaterIntakeUseCase,
    private val updateWaterIntakeUseCase: UpdateWaterIntakeUseCase,
    private val updateSleepUseCase: UpdateSleepUseCase,
    private val updateStepsUseCase: UpdateStepsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DailyLogUiState())
    val uiState: StateFlow<DailyLogUiState> = _uiState.asStateFlow()

    init {
        loadTodayLog()
    }

    fun onAddWater(ml: Int) {
        viewModelScope.launch {
            addWaterIntakeUseCase(ml)
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Failed to update water intake")
                    }
                }
        }
    }

    fun onWaterChanged(ml: Int) {
        viewModelScope.launch {
            updateWaterIntakeUseCase(ml)
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Failed to update water intake")
                    }
                }
        }
    }

    fun onSleepChanged(minutes: Int) {
        viewModelScope.launch {
            updateSleepUseCase(minutes)
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Failed to update sleep")
                    }
                }
        }
    }

    fun onStepsChanged(steps: Int) {
        viewModelScope.launch {
            updateStepsUseCase(steps)
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Failed to update steps")
                    }
                }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadTodayLog() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getTodayLogUseCase()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Failed to load today's log",
                        )
                    }
                }
                .collect { log ->
                    _uiState.value = if (log != null) {
                        DailyLogUiState(
                            isLoading = false,
                            steps = log.steps,
                            waterIntakeMl = log.waterIntakeMl,
                            sleepMinutes = log.sleepMinutes,
                            activeCalories = log.activeCalories,
                            workoutMinutes = log.workoutMinutes,
                            distanceKm = "%.2f".format(log.distanceMeters / 1000f),
                            restingHeartRate = log.restingHeartRate,
                            averageHeartRate = log.averageHeartRate,
                            readinessScore = log.readinessScore,
                            notes = log.notes,
                        )
                    } else {
                        DailyLogUiState(isLoading = false)
                    }
                }
        }
    }
}
