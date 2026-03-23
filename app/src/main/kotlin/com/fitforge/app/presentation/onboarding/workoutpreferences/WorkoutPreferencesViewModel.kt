package com.fitforge.app.presentation.onboarding.workoutpreferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.onboarding.GetWorkoutPreferencesDraftUseCase
import com.fitforge.app.domain.usecase.onboarding.GetWorkoutPreferencesOptionsUseCase
import com.fitforge.app.domain.usecase.onboarding.SaveWorkoutPreferencesUseCase
import com.fitforge.app.domain.usecase.onboarding.ValidateWorkoutPreferencesUseCase
import com.fitforge.app.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WorkoutPreferencesViewModel @Inject constructor(
    private val getWorkoutPreferencesOptionsUseCase: GetWorkoutPreferencesOptionsUseCase,
    private val getWorkoutPreferencesDraftUseCase: GetWorkoutPreferencesDraftUseCase,
    private val validateWorkoutPreferencesUseCase: ValidateWorkoutPreferencesUseCase,
    private val saveWorkoutPreferencesUseCase: SaveWorkoutPreferencesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutPreferencesUiState())
    val uiState: StateFlow<WorkoutPreferencesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val (locationOptions, equipmentOptions) = getWorkoutPreferencesOptionsUseCase()
            val draft = getWorkoutPreferencesDraftUseCase()
            _uiState.update {
                it.copy(
                    locationOptions = locationOptions,
                    equipmentOptions = equipmentOptions,
                    selectedLocations = draft.selectedLocations,
                    selectedEquipment = draft.selectedEquipment,
                )
            }
        }
    }

    fun onLocationToggle(value: String) {
        _uiState.update { state ->
            val next = state.selectedLocations.toMutableSet().apply {
                if (!add(value)) remove(value)
            }
            state.copy(selectedLocations = next, locationError = null)
        }
    }

    fun onEquipmentToggle(value: String) {
        _uiState.update { state ->
            val next = state.selectedEquipment.toMutableSet().apply {
                if (value == "none") {
                    clear()
                    add(value)
                } else {
                    remove("none")
                    if (!add(value)) remove(value)
                }
            }
            state.copy(selectedEquipment = next)
        }
    }

    fun onContinueClick() {
        val current = uiState.value
        val validation = validateWorkoutPreferencesUseCase(current.selectedLocations)
        if (!validation.isValid) {
            _uiState.update { it.copy(locationError = validation.locationError) }
            return
        }

        viewModelScope.launch {
            saveWorkoutPreferencesUseCase(
                selectedLocations = current.selectedLocations,
                selectedEquipment = current.selectedEquipment,
            )
            _uiState.update { it.copy(destinationRoute = Screen.ScheduleSetup.route) }
        }
    }
}
