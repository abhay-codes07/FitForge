package com.fitforge.app.presentation.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.workouts.BuildAndCreateCustomWorkoutUseCase
import com.fitforge.app.domain.usecase.workouts.CreateCustomWorkoutDraft
import com.fitforge.app.domain.usecase.workouts.SearchExercisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CreateCustomWorkoutViewModel @Inject constructor(
    private val searchExercisesUseCase: SearchExercisesUseCase,
    private val buildAndCreateCustomWorkoutUseCase: BuildAndCreateCustomWorkoutUseCase,
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val _uiState = MutableStateFlow(CreateCustomWorkoutUiState())
    val uiState: StateFlow<CreateCustomWorkoutUiState> = _uiState.asStateFlow()

    init {
        observeExercises()
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value, errorMessage = null, successMessage = null) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value, errorMessage = null, successMessage = null) }
    }

    fun onSearchQueryChanged(value: String) {
        query.value = value
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun onWorkoutTypeSelected(value: String) {
        _uiState.update { it.copy(selectedWorkoutType = value) }
    }

    fun onDifficultySelected(value: String) {
        _uiState.update { it.copy(selectedDifficulty = value) }
    }

    fun onDurationChanged(value: String) {
        _uiState.update { it.copy(durationMinutes = value.filter(Char::isDigit).take(3)) }
    }

    fun onExerciseToggled(exerciseId: String) {
        _uiState.update { state ->
            val updated = state.selectedExerciseIds.toMutableSet().apply {
                if (contains(exerciseId)) remove(exerciseId) else add(exerciseId)
            }
            state.copy(selectedExerciseIds = updated)
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val state = _uiState.value
            val duration = state.durationMinutes.toIntOrNull() ?: 30
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

            runCatching {
                buildAndCreateCustomWorkoutUseCase(
                    CreateCustomWorkoutDraft(
                        name = state.name,
                        description = state.description,
                        workoutType = state.selectedWorkoutType,
                        difficulty = state.selectedDifficulty,
                        estimatedDurationMinutes = duration,
                        selectedExerciseIds = state.selectedExerciseIds.toList(),
                    ),
                )
            }.onSuccess { workoutId ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = "Workout created ($workoutId)",
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Unable to create workout",
                    )
                }
            }
        }
    }

    private fun observeExercises() {
        viewModelScope.launch {
            query.flatMapLatest { q ->
                _uiState.update { it.copy(isLoading = true) }
                searchExercisesUseCase(q)
            }.catch { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load exercises",
                    )
                }
            }.collect { exercises ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        exercises = exercises.map { exercise ->
                            CustomWorkoutExerciseItem(
                                id = exercise.id,
                                name = exercise.name,
                                category = exercise.category,
                            )
                        },
                    )
                }
            }
        }
    }
}
