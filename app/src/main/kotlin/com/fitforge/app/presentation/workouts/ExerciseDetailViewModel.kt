package com.fitforge.app.presentation.workouts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.workouts.GetExerciseDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getExerciseDetailUseCase: GetExerciseDetailUseCase,
) : ViewModel() {
    private val exerciseId: String = checkNotNull(savedStateHandle["exerciseId"])

    private val _uiState = MutableStateFlow(ExerciseDetailUiState())
    val uiState: StateFlow<ExerciseDetailUiState> = _uiState.asStateFlow()

    init {
        observeExerciseDetail()
    }

    private fun observeExerciseDetail() {
        viewModelScope.launch {
            getExerciseDetailUseCase(exerciseId)
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unable to load exercise",
                        )
                    }
                }
                .collect { detail ->
                    if (detail == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Exercise not found",
                            )
                        }
                    } else {
                        _uiState.value = ExerciseDetailUiState(
                            isLoading = false,
                            title = detail.name,
                            description = detail.description,
                            category = detail.category,
                            difficulty = detail.difficulty,
                            durationLabel = detail.durationLabel,
                            caloriesLabel = detail.caloriesLabel,
                            equipment = detail.equipment,
                            primaryMuscles = detail.primaryMuscles,
                            instructions = detail.instructions,
                        )
                    }
                }
        }
    }
}
