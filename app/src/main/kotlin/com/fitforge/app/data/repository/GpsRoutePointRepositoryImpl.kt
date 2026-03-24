package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.GpsRoutePointDao
import com.fitforge.app.data.local.db.entity.GpsRoutePointEntity
import com.fitforge.app.domain.repository.GpsRoutePointRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GpsRoutePointRepositoryImpl @Inject constructor(
    private val gpsRoutePointDao: GpsRoutePointDao,
) : GpsRoutePointRepository {
    override fun observeRoutePoints(workoutId: String): Flow<List<GpsRoutePointEntity>> = gpsRoutePointDao.observeRoutePoints(workoutId)

    override suspend fun getRoutePoints(workoutId: String): List<GpsRoutePointEntity> = gpsRoutePointDao.getRoutePoints(workoutId)

    override suspend fun insertRoutePoint(point: GpsRoutePointEntity): Long = gpsRoutePointDao.insert(point)

    override suspend fun insertRoutePoints(points: List<GpsRoutePointEntity>): List<Long> = gpsRoutePointDao.insertAll(points)

    override suspend fun deleteRoutePointsByWorkoutId(workoutId: String) = gpsRoutePointDao.deleteByWorkoutId(workoutId)

    override suspend fun deleteRoutePoint(point: GpsRoutePointEntity) = gpsRoutePointDao.delete(point)
}
