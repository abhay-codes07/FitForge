package com.fitforge.app.presentation.gps_tracking

import com.fitforge.app.data.local.db.entity.GpsRoutePointEntity

data class GpsTrackingUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedMode: String = "Run",
    val availableModes: List<String> = listOf("Run", "Walk", "Cycle"),
    val isTracking: Boolean = false,
    val activeWorkoutId: String? = null,
    val routePoints: List<GpsRoutePointEntity> = emptyList(),
    val distanceMeters: Float = 0f,
    val elapsedSeconds: Long = 0L,
    val currentSpeedMetersPerSecond: Float = 0f,
    val averagePaceMinPerKm: Float = 0f,
    val pendingStartRequest: GpsServiceStartRequest? = null,
    val pendingStopRequest: Boolean = false,
)

data class GpsServiceStartRequest(
    val workoutId: String,
    val mode: String,
)
