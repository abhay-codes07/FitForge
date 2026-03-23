package com.fitforge.app.presentation.onboarding.bodymetrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.onboarding.GetBodyMetricsDraftUseCase
import com.fitforge.app.domain.usecase.onboarding.SaveBodyMetricsUseCase
import com.fitforge.app.domain.usecase.onboarding.ValidateBodyMetricsUseCase
import com.fitforge.app.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BodyMetricsViewModel @Inject constructor(
    private val getBodyMetricsDraftUseCase: GetBodyMetricsDraftUseCase,
    private val validateBodyMetricsUseCase: ValidateBodyMetricsUseCase,
    private val saveBodyMetricsUseCase: SaveBodyMetricsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BodyMetricsUiState())
    val uiState: StateFlow<BodyMetricsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val draft = getBodyMetricsDraftUseCase()
            _uiState.update {
                it.copy(
                    unitSystem = draft.unitSystem,
                    height = draft.height,
                    weight = draft.weight,
                    age = draft.age,
                    gender = draft.gender,
                )
            }
        }
    }

    fun onUnitSystemSelected(unitSystem: BodyMetricsUnitSystem) {
        _uiState.update { current ->
            current.copy(
                unitSystem = unitSystem,
                heightError = null,
                weightError = null,
                ageError = null,
            )
        }
    }

    fun onHeightChanged(value: String) {
        _uiState.update { it.copy(height = value, heightError = null) }
    }

    fun onWeightChanged(value: String) {
        _uiState.update { it.copy(weight = value, weightError = null) }
    }

    fun onAgeChanged(value: String) {
        _uiState.update { it.copy(age = value, ageError = null) }
    }

    fun onGenderSelected(gender: BodyMetricsGender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onContinueClick() {
        val current = uiState.value
        val validation = validateBodyMetricsUseCase(
            unitSystem = current.unitSystem,
            height = current.height,
            weight = current.weight,
            age = current.age,
        )

        if (!validation.isValid) {
            _uiState.update {
                it.copy(
                    heightError = validation.heightError,
                    weightError = validation.weightError,
                    ageError = validation.ageError,
                )
            }
            return
        }

        viewModelScope.launch {
            saveBodyMetricsUseCase(
                unitSystem = current.unitSystem,
                height = current.height,
                weight = current.weight,
                age = current.age,
                gender = current.gender,
            )
            _uiState.update { it.copy(destinationRoute = Screen.FitnessLevel.route) }
        }
    }
}
