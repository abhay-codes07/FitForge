package com.fitforge.app.presentation.programs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.programs.GetProgramsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProgramsViewModel @Inject constructor(
    private val getProgramsUseCase: GetProgramsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProgramsUiState())
    val uiState: StateFlow<ProgramsUiState> = _uiState.asStateFlow()

    init {
        loadPrograms("all")
    }

    fun onGoalSelected(goal: String) {
        _uiState.update { it.copy(selectedGoal = goal) }
        loadPrograms(goal)
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadPrograms(goal: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val flow = if (goal == "all") {
                getProgramsUseCase()
            } else {
                getProgramsUseCase(goal)
            }

            flow.catch { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load programs",
                    )
                }
            }.collect { programs ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    programs = programs,
                    errorMessage = null,
                )
            }
        }
    }
}
