package com.fitforge.app.presentation.gps_tracking

import com.fitforge.app.data.local.db.entity.GpsRoutePointEntity
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GpsMetricsCalculatorTest {
    @Test
    fun `distance is positive for moved points`() {
        val points = listOf(
            point(12.9716, 77.5946, 1000L),
            point(12.9722, 77.5951, 5000L),
        )

        val distance = GpsMetricsCalculator.calculateDistanceMeters(points)

        assertTrue(distance > 0f)
    }

    @Test
    fun `speed is positive for moved points with time gap`() {
        val points = listOf(
            point(12.9716, 77.5946, 1000L),
            point(12.9722, 77.5951, 5000L),
        )

        val speed = GpsMetricsCalculator.calculateCurrentSpeedMetersPerSecond(points)

        assertTrue(speed > 0f)
    }

    @Test
    fun `pace is zero when distance is zero`() {
        val pace = GpsMetricsCalculator.calculateAveragePaceMinPerKm(distanceMeters = 0f, elapsedSeconds = 300)
        assertTrue(pace == 0f)
    }

    private fun point(lat: Double, lon: Double, time: Long): GpsRoutePointEntity = GpsRoutePointEntity(
        workoutId = "w1",
        latitude = lat,
        longitude = lon,
        altitudeMeters = null,
        accuracyMeters = 3f,
        speedMetersPerSecond = null,
        heartRate = null,
        timestampEpochMillis = time,
        segmentIndex = 0,
    )
}
