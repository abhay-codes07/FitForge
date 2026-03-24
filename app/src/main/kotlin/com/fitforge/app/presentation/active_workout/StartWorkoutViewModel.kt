package com.fitforge.app.presentation.active_workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.active_workout.GetStartWorkoutOptionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StartWorkoutViewModel @Inject constructor(
    private val getStartWorkoutOptionsUseCase: GetStartWorkoutOptionsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StartWorkoutUiState())
    val uiState: StateFlow<StartWorkoutUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun onOptionSelected(option: String) {
        _uiState.update { it.copy(selectedOption = option) }
    }

    fun retry() {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { getStartWorkoutOptionsUseCase() }
                .onSuccess { data ->
                    _uiState.value = StartWorkoutUiState(
                        isLoading = false,
                        options = data.options,
                        scheduledWorkoutCountLabel = if (data.scheduledWorkoutCount > 0) {
                            "${data.scheduledWorkoutCount} scheduled this week"
                        } else {
                            "No scheduled workouts this week"
                        },
                    )
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unable to load start options",
                        )
                    }
                }
        }
    }
}
