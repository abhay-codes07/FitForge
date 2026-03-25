package com.fitforge.app.presentation.gps_tracking

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.data.local.db.entity.GpsRoutePointEntity
import com.fitforge.app.domain.usecase.gps_tracking.CompleteGpsWorkoutSessionUseCase
import com.fitforge.app.domain.usecase.gps_tracking.CreateGpsWorkoutSessionUseCase
import com.fitforge.app.domain.usecase.gps_tracking.ObserveGpsRoutePointsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GpsTrackingViewModel @Inject constructor(
    private val createGpsWorkoutSessionUseCase: CreateGpsWorkoutSessionUseCase,
    private val observeGpsRoutePointsUseCase: ObserveGpsRoutePointsUseCase,
    private val completeGpsWorkoutSessionUseCase: CompleteGpsWorkoutSessionUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GpsTrackingUiState())
    val uiState: StateFlow<GpsTrackingUiState> = _uiState.asStateFlow()

    private var observeRouteJob: Job? = null
    private var elapsedTickerJob: Job? = null

    fun onModeSelected(mode: String) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    fun onStartTrackingClick() {
        if (_uiState.value.isTracking) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                createGpsWorkoutSessionUseCase(_uiState.value.selectedMode)
            }.onSuccess { workoutId ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isTracking = true,
                        activeWorkoutId = workoutId,
                        distanceMeters = 0f,
                        elapsedSeconds = 0L,
                        currentSpeedMetersPerSecond = 0f,
                        averagePaceMinPerKm = 0f,
                        routePoints = emptyList(),
                        pendingStartRequest = GpsServiceStartRequest(workoutId, it.selectedMode),
                    )
                }
                observeRoutePoints(workoutId)
                startElapsedTicker()
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to start GPS tracking",
                    )
                }
            }
        }
    }

    fun onStopTrackingClick() {
        if (!_uiState.value.isTracking) return
        elapsedTickerJob?.cancel()
        observeRouteJob?.cancel()
        _uiState.update {
            it.copy(
                isTracking = false,
                pendingStopRequest = true,
            )
        }
    }

    fun onServiceStartHandled() {
        _uiState.update { it.copy(pendingStartRequest = null) }
    }

    fun onServiceStopHandled() {
        _uiState.update { it.copy(pendingStopRequest = false) }
    }

    fun onServiceStopped(durationSeconds: Long, distanceMeters: Float) {
        val workoutId = _uiState.value.activeWorkoutId ?: return
        viewModelScope.launch {
            completeGpsWorkoutSessionUseCase(
                workoutId = workoutId,
                durationSeconds = durationSeconds,
                distanceMeters = distanceMeters,
            )
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        observeRouteJob?.cancel()
        elapsedTickerJob?.cancel()
        super.onCleared()
    }

    private fun observeRoutePoints(workoutId: String) {
        observeRouteJob?.cancel()
        observeRouteJob = viewModelScope.launch {
            observeGpsRoutePointsUseCase(workoutId).collect { points ->
                val distanceMeters = calculateDistance(points)
                val currentSpeed = calculateCurrentSpeed(points)
                val elapsed = _uiState.value.elapsedSeconds.coerceAtLeast(1L)
                val avgPace = if (distanceMeters <= 0f) 0f else {
                    val distanceKm = distanceMeters / 1000f
                    (elapsed / 60f) / distanceKm
                }

                _uiState.update {
                    it.copy(
                        routePoints = points,
                        distanceMeters = distanceMeters,
                        currentSpeedMetersPerSecond = currentSpeed,
                        averagePaceMinPerKm = avgPace,
                    )
                }
            }
        }
    }

    private fun startElapsedTicker() {
        elapsedTickerJob?.cancel()
        elapsedTickerJob = viewModelScope.launch {
            while (_uiState.value.isTracking) {
                delay(1000)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    private fun calculateDistance(points: List<GpsRoutePointEntity>): Float {
        if (points.size < 2) return 0f

        var total = 0f
        for (i in 1 until points.size) {
            val prev = points[i - 1]
            val current = points[i]
            val results = FloatArray(1)
            Location.distanceBetween(
                prev.latitude,
                prev.longitude,
                current.latitude,
                current.longitude,
                results,
            )
            total += results[0]
        }
        return total
    }

    private fun calculateCurrentSpeed(points: List<GpsRoutePointEntity>): Float {
        if (points.size < 2) return 0f

        val prev = points[points.lastIndex - 1]
        val current = points.last()
        val results = FloatArray(1)
        Location.distanceBetween(
            prev.latitude,
            prev.longitude,
            current.latitude,
            current.longitude,
            results,
        )
        val dtMillis = (current.timestampEpochMillis - prev.timestampEpochMillis).coerceAtLeast(1L)
        return results[0] / (dtMillis / 1000f)
    }
}
