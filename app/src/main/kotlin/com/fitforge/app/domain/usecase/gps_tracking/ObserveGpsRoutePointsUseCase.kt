package com.fitforge.app.domain.usecase.gps_tracking

import com.fitforge.app.data.local.db.entity.GpsRoutePointEntity
import com.fitforge.app.domain.repository.GpsRoutePointRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveGpsRoutePointsUseCase @Inject constructor(
    private val gpsRoutePointRepository: GpsRoutePointRepository,
) {
    operator fun invoke(workoutId: String): Flow<List<GpsRoutePointEntity>> =
        gpsRoutePointRepository.observeRoutePoints(workoutId)
}
