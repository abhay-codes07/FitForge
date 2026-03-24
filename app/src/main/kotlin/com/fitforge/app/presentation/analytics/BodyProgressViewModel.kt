package com.fitforge.app.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.analytics.AddBodyMeasurementUseCase
import com.fitforge.app.domain.usecase.analytics.GetBodyProgressDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class BodyProgressViewModel @Inject constructor(
    private val getBodyProgressDataUseCase: GetBodyProgressDataUseCase,
    private val addBodyMeasurementUseCase: AddBodyMeasurementUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BodyProgressUiState())
    val uiState: StateFlow<BodyProgressUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)

    init {
        load(90)
    }

    fun onRangeSelected(days: Int) {
        load(days)
    }

    fun onWeightChanged(value: String) {
        _uiState.update { it.copy(weightInput = value, errorMessage = null, successMessage = null) }
    }

    fun onBodyFatChanged(value: String) {
        _uiState.update { it.copy(bodyFatInput = value, errorMessage = null, successMessage = null) }
    }

    fun onPhotoUriChanged(value: String) {
        _uiState.update { it.copy(photoUriInput = value, errorMessage = null, successMessage = null) }
    }

    fun onNoteChanged(value: String) {
        _uiState.update { it.copy(noteInput = value, errorMessage = null, successMessage = null) }
    }

    fun onSaveMeasurement() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

            runCatching {
                addBodyMeasurementUseCase(
                    weightKg = state.weightInput.toFloatOrNull(),
                    bodyFatPercent = state.bodyFatInput.toFloatOrNull(),
                    photoUri = state.photoUriInput,
                    notes = state.noteInput.takeIf { it.isNotBlank() },
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        weightInput = "",
                        bodyFatInput = "",
                        photoUriInput = "",
                        noteInput = "",
                        successMessage = "Measurement saved",
                    )
                }
                load(_uiState.value.selectedRangeDays)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Unable to save measurement",
                    )
                }
            }
        }
    }

    private fun load(days: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedRangeDays = days, errorMessage = null) }
            runCatching {
                getBodyProgressDataUseCase(rangeDays = days)
            }.onSuccess { data ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        selectedRangeDays = days,
                        latestWeightLabel = data.latestWeightKg?.let { value -> "${"%.1f".format(value)} kg" } ?: "--",
                        latestBodyFatLabel = data.latestBodyFatPercent?.let { value -> "${"%.1f".format(value)} %" } ?: "--",
                        weightTrend = data.weightTrend,
                        bodyFatTrend = data.bodyFatTrend,
                        photoUris = data.photoUris,
                        entries = data.entries.map { entry ->
                            BodyMeasurementEntryUi(
                                id = entry.id,
                                recordedAtLabel = dateFormat.format(Date(entry.recordedAtEpochMillis)),
                                weightLabel = entry.weightKg?.let { value -> "${"%.1f".format(value)} kg" } ?: "--",
                                bodyFatLabel = entry.bodyFatPercent?.let { value -> "${"%.1f".format(value)} %" } ?: "--",
                            )
                        }.reversed(),
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load body progress",
                    )
                }
            }
        }
    }
}
