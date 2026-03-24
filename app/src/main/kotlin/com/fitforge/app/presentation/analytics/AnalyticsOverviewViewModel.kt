package com.fitforge.app.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.analytics.GetAnalyticsOverviewDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AnalyticsOverviewViewModel @Inject constructor(
    private val getAnalyticsOverviewDataUseCase: GetAnalyticsOverviewDataUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AnalyticsOverviewUiState())
    val uiState: StateFlow<AnalyticsOverviewUiState> = _uiState.asStateFlow()

    init {
        load(rangeDays = 30)
    }

    fun onRangeSelected(rangeDays: Int) {
        load(rangeDays)
    }

    fun retry() {
        load(_uiState.value.selectedRangeDays)
    }

    private fun load(rangeDays: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    selectedRangeDays = rangeDays,
                    errorMessage = null,
                )
            }

            runCatching { getAnalyticsOverviewDataUseCase(rangeDays = rangeDays) }
                .onSuccess { data ->
                    _uiState.value = AnalyticsOverviewUiState(
                        isLoading = false,
                        selectedRangeDays = data.rangeDays,
                        totalSteps = data.totalSteps.toString(),
                        totalCalories = "${data.totalCalories} kcal",
                        totalWorkoutMinutes = "${data.totalWorkoutMinutes} min",
                        completedWorkouts = data.completedWorkouts.toString(),
                        averageWorkoutMinutes = "${data.averageWorkoutMinutes} min/day",
                        weightChangeLabel = data.weightChangeKg?.let { "${"%.1f".format(it)} kg" } ?: "--",
                        stepTrend = data.stepTrend,
                    )
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unable to load analytics",
                        )
                    }
                }
        }
    }
}
