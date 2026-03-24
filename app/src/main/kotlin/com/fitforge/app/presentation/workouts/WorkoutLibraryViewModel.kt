package com.fitforge.app.presentation.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.workouts.GetWorkoutLibraryExercisesUseCase
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
class WorkoutLibraryViewModel @Inject constructor(
    private val getWorkoutLibraryExercisesUseCase: GetWorkoutLibraryExercisesUseCase,
) : ViewModel() {
    private data class QueryFilters(
        val query: String,
        val category: String?,
        val difficulty: String?,
    )

    private val queryFilters = MutableStateFlow(QueryFilters(query = "", category = null, difficulty = null))
    private val _uiState = MutableStateFlow(WorkoutLibraryUiState())
    val uiState: StateFlow<WorkoutLibraryUiState> = _uiState.asStateFlow()

    init {
        observeLibrary()
    }

    fun onQueryChanged(query: String) {
        queryFilters.update { it.copy(query = query) }
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: String?) {
        val nextCategory = if (_uiState.value.selectedCategory == category) null else category
        queryFilters.update { it.copy(category = nextCategory) }
        _uiState.update { it.copy(selectedCategory = nextCategory) }
    }

    fun onDifficultySelected(difficulty: String?) {
        val nextDifficulty = if (_uiState.value.selectedDifficulty == difficulty) null else difficulty
        queryFilters.update { it.copy(difficulty = nextDifficulty) }
        _uiState.update { it.copy(selectedDifficulty = nextDifficulty) }
    }

    fun retry() {
        queryFilters.update { it.copy() }
    }

    private fun observeLibrary() {
        viewModelScope.launch {
            queryFilters
                .flatMapLatest { filters ->
                    _uiState.update {
                        it.copy(
                            isLoading = true,
                            errorMessage = null,
                        )
                    }
                    getWorkoutLibraryExercisesUseCase(
                        query = filters.query,
                        selectedCategory = filters.category,
                        selectedDifficulty = filters.difficulty,
                    )
                }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unable to load workout library",
                        )
                    }
                }
                .collect { exercises ->
                    val categories = exercises.map { it.category }.distinct().sorted()
                    val difficulties = exercises.map { it.difficulty }.distinct().sorted()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            categoryFilters = categories,
                            difficultyFilters = difficulties,
                            exercises = exercises.map { exercise ->
                                WorkoutLibraryExerciseItem(
                                    id = exercise.id,
                                    name = exercise.name,
                                    category = exercise.category,
                                    difficulty = exercise.difficulty,
                                    durationLabel = exercise.estimatedDurationSeconds?.let { seconds ->
                                        "${seconds / 60} min"
                                    } ?: "--",
                                    caloriesLabel = exercise.estimatedCalories?.let { calories ->
                                        "$calories kcal"
                                    } ?: "--",
                                    equipment = exercise.equipment.toList().sorted(),
                                )
                            },
                        )
                    }
                }
        }
    }
}
