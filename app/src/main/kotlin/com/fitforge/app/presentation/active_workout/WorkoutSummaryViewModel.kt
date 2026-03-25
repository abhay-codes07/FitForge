package com.fitforge.app.presentation.active_workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.active_workout.GetWorkoutSummaryDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WorkoutSummaryViewModel @Inject constructor(
    private val getWorkoutSummaryDataUseCase: GetWorkoutSummaryDataUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutSummaryUiState())
    val uiState: StateFlow<WorkoutSummaryUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() {
        load()
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = WorkoutSummaryUiState(isLoading = true)
            runCatching { getWorkoutSummaryDataUseCase() }
                .onSuccess { data ->
                    val completedAtLabel = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                        .format(Date(data.completedAtEpochMillis))

                    val shareText = "I completed ${data.workoutName} in ${data.estimatedDurationMinutes} min " +
                        "with ${data.totalSets} sets and ${data.totalReps} reps on FitForge."

                    _uiState.value = WorkoutSummaryUiState(
                        isLoading = false,
                        workoutName = data.workoutName,
                        completedAtLabel = completedAtLabel,
                        durationLabel = "${data.estimatedDurationMinutes} min",
                        caloriesLabel = "${data.estimatedCalories} kcal",
                        totalSetsLabel = data.totalSets.toString(),
                        totalRepsLabel = data.totalReps.toString(),
                        totalVolumeLabel = String.format(Locale.US, "%.1f kg", data.totalVolumeKg),
                        personalRecordsLabel = data.personalRecordCount.toString(),
                        shareText = shareText,
                    )
                }
                .onFailure { throwable ->
                    _uiState.value = WorkoutSummaryUiState(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load workout summary",
                    )
                }
        }
    }
}
