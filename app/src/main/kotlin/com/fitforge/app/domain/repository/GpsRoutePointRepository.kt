package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.GpsRoutePointEntity
import kotlinx.coroutines.flow.Flow

interface GpsRoutePointRepository {
    fun observeRoutePoints(workoutId: String): Flow<List<GpsRoutePointEntity>>
    suspend fun getRoutePoints(workoutId: String): List<GpsRoutePointEntity>
    suspend fun insertRoutePoint(point: GpsRoutePointEntity): Long
    suspend fun insertRoutePoints(points: List<GpsRoutePointEntity>): List<Long>
    suspend fun deleteRoutePointsByWorkoutId(workoutId: String)
    suspend fun deleteRoutePoint(point: GpsRoutePointEntity)
}
