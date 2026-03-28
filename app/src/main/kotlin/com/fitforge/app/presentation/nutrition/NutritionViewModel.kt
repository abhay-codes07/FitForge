package com.fitforge.app.presentation.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.nutrition.DeleteMealUseCase
import com.fitforge.app.domain.usecase.nutrition.GetDailyNutritionSummaryUseCase
import com.fitforge.app.domain.usecase.nutrition.GetMealsForDateUseCase
import com.fitforge.app.domain.usecase.nutrition.GetMealsForTodayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val getMealsForTodayUseCase: GetMealsForTodayUseCase,
    private val getMealsForDateUseCase: GetMealsForDateUseCase,
    private val getDailyNutritionSummaryUseCase: GetDailyNutritionSummaryUseCase,
    private val deleteMealUseCase: DeleteMealUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NutritionUiState(selectedDateEpochDay = LocalDate.now().toEpochDay())
    )
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()

    init {
        loadNutritionData()
    }

    fun selectDate(dateEpochDay: Long) {
        _uiState.update { it.copy(selectedDateEpochDay = dateEpochDay) }
        loadNutritionData()
    }

    fun deleteMeal(mealId: String) {
        viewModelScope.launch {
            deleteMealUseCase(mealId)
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Failed to delete meal")
                    }
                }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadNutritionData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val dateEpochDay = _uiState.value.selectedDateEpochDay

            // Load meals
            launch {
                getMealsForDateUseCase(dateEpochDay)
                    .catch { throwable ->
                        _uiState.update {
                            it.copy(errorMessage = throwable.message ?: "Failed to load meals")
                        }
                    }
                    .collect { meals ->
                        _uiState.update { it.copy(meals = meals) }
                    }
            }

            // Load summary
            launch {
                getDailyNutritionSummaryUseCase(dateEpochDay)
                    .catch { throwable ->
                        _uiState.update {
                            it.copy(errorMessage = throwable.message ?: "Failed to load summary")
                        }
                    }
                    .collect { summary ->
                        _uiState.update { it.copy(summary = summary, isLoading = false) }
                    }
            }
        }
    }
}
