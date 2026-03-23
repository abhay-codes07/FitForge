package com.fitforge.app.presentation.onboarding.fitnesslevel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.domain.usecase.onboarding.GetFitnessLevelOptionsUseCase
import com.fitforge.app.domain.usecase.onboarding.GetSelectedFitnessLevelUseCase
import com.fitforge.app.domain.usecase.onboarding.SaveFitnessLevelUseCase
import com.fitforge.app.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FitnessLevelViewModel @Inject constructor(
    private val getFitnessLevelOptionsUseCase: GetFitnessLevelOptionsUseCase,
    private val getSelectedFitnessLevelUseCase: GetSelectedFitnessLevelUseCase,
    private val saveFitnessLevelUseCase: SaveFitnessLevelUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FitnessLevelUiState())
    val uiState: StateFlow<FitnessLevelUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val options = getFitnessLevelOptionsUseCase()
            val savedLevel = getSelectedFitnessLevelUseCase()
                .takeIf { it != UserPrefs.FitnessLevel.UNSPECIFIED }
            _uiState.update {
                it.copy(
                    options = options,
                    selectedLevel = savedLevel,
                )
            }
        }
    }

    fun onFitnessLevelSelected(value: String) {
        _uiState.update { it.copy(selectedLevel = value) }
    }

    fun onContinueClick() {
        val selectedLevel = uiState.value.selectedLevel ?: return
        viewModelScope.launch {
            saveFitnessLevelUseCase(selectedLevel)
            _uiState.update { it.copy(destinationRoute = Screen.WorkoutPreferences.route) }
        }
    }
}
