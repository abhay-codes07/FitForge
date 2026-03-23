package com.fitforge.app.presentation.onboarding.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.onboarding.GetGoalOptionsUseCase
import com.fitforge.app.domain.usecase.onboarding.GetSelectedGoalsUseCase
import com.fitforge.app.domain.usecase.onboarding.SaveGoalSelectionUseCase
import com.fitforge.app.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GoalSelectionViewModel @Inject constructor(
    private val getGoalOptionsUseCase: GetGoalOptionsUseCase,
    private val getSelectedGoalsUseCase: GetSelectedGoalsUseCase,
    private val saveGoalSelectionUseCase: SaveGoalSelectionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalSelectionUiState())
    val uiState: StateFlow<GoalSelectionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val selected = getSelectedGoalsUseCase()
            _uiState.update {
                it.copy(
                    options = getGoalOptionsUseCase(),
                    selectedGoals = selected,
                    canContinue = selected.isNotEmpty(),
                )
            }
        }
    }

    fun onGoalToggle(goalId: String) {
        _uiState.update { current ->
            val updated = current.selectedGoals.toMutableSet().apply {
                if (!add(goalId)) remove(goalId)
            }.toSet()
            current.copy(
                selectedGoals = updated,
                canContinue = updated.isNotEmpty(),
            )
        }
    }

    fun onContinueClick() {
        val selected = uiState.value.selectedGoals
        if (selected.isEmpty()) return

        viewModelScope.launch {
            saveGoalSelectionUseCase(selected)
            _uiState.update { current ->
                current.copy(destinationRoute = Screen.BodyMetrics.route)
            }
        }
    }
}
