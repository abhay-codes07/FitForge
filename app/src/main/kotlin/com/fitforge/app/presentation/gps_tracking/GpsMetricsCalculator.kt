package com.fitforge.app.presentation.gps_tracking

import com.fitforge.app.data.local.db.entity.GpsRoutePointEntity
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object GpsMetricsCalculator {
    fun calculateDistanceMeters(points: List<GpsRoutePointEntity>): Float {
        if (points.size < 2) return 0f

        var total = 0f
        for (i in 1 until points.size) {
            total += haversineMeters(points[i - 1], points[i])
        }
        return total
    }

    fun calculateCurrentSpeedMetersPerSecond(points: List<GpsRoutePointEntity>): Float {
        if (points.size < 2) return 0f

        val prev = points[points.lastIndex - 1]
        val current = points.last()
        val dtMillis = (current.timestampEpochMillis - prev.timestampEpochMillis).coerceAtLeast(1L)
        return haversineMeters(prev, current) / (dtMillis / 1000f)
    }

    fun calculateAveragePaceMinPerKm(distanceMeters: Float, elapsedSeconds: Long): Float {
        if (distanceMeters <= 0f) return 0f
        val distanceKm = distanceMeters / 1000f
        return (elapsedSeconds / 60f) / distanceKm
    }

    private fun haversineMeters(a: GpsRoutePointEntity, b: GpsRoutePointEntity): Float {
        val earthRadiusMeters = 6371000.0
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)
        val dLat = lat2 - lat1
        val dLon = Math.toRadians(b.longitude - a.longitude)

        val sinLat = sin(dLat / 2)
        val sinLon = sin(dLon / 2)
        val h = sinLat * sinLat + cos(lat1) * cos(lat2) * sinLon * sinLon
        val c = 2 * atan2(sqrt(h), sqrt(1 - h))

        return (earthRadiusMeters * c).toFloat()
    }
}
