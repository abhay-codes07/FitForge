package com.fitforge.app.presentation.onboarding.schedulesetup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.onboarding.GetScheduleOptionsUseCase
import com.fitforge.app.domain.usecase.onboarding.GetScheduleSetupDraftUseCase
import com.fitforge.app.domain.usecase.onboarding.SaveScheduleSetupUseCase
import com.fitforge.app.domain.usecase.onboarding.ValidateScheduleSetupUseCase
import com.fitforge.app.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ScheduleSetupViewModel @Inject constructor(
    private val getScheduleOptionsUseCase: GetScheduleOptionsUseCase,
    private val getScheduleSetupDraftUseCase: GetScheduleSetupDraftUseCase,
    private val validateScheduleSetupUseCase: ValidateScheduleSetupUseCase,
    private val saveScheduleSetupUseCase: SaveScheduleSetupUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleSetupUiState())
    val uiState: StateFlow<ScheduleSetupUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val options = getScheduleOptionsUseCase()
            val draft = getScheduleSetupDraftUseCase()
            _uiState.update {
                it.copy(
                    dayOptions = options,
                    selectedDays = draft.selectedDays,
                    durationMinutes = draft.durationMinutes,
                )
            }
        }
    }

    fun onDayToggle(value: String) {
        _uiState.update { state ->
            val next = state.selectedDays.toMutableSet().apply {
                if (!add(value)) remove(value)
            }
            state.copy(selectedDays = next, dayError = null)
        }
    }

    fun onDurationChanged(value: Float) {
        _uiState.update { it.copy(durationMinutes = value.toInt()) }
    }

    fun onContinueClick() {
        val current = uiState.value
        val validation = validateScheduleSetupUseCase(current.selectedDays)
        if (!validation.isValid) {
            _uiState.update { it.copy(dayError = validation.dayError) }
            return
        }

        viewModelScope.launch {
            saveScheduleSetupUseCase(
                selectedDays = current.selectedDays,
                durationMinutes = current.durationMinutes,
            )
            _uiState.update { it.copy(destinationRoute = Screen.Permissions.route) }
        }
    }
}
