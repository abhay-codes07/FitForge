package com.fitforge.app.domain.usecase.gps_tracking

import com.fitforge.app.data.local.db.entity.GpsRoutePointEntity
import com.fitforge.app.domain.repository.GpsRoutePointRepository
import javax.inject.Inject

class RecordGpsRoutePointUseCase @Inject constructor(
    private val gpsRoutePointRepository: GpsRoutePointRepository,
) {
    suspend operator fun invoke(point: GpsRoutePointEntity) {
        gpsRoutePointRepository.insertRoutePoint(point)
    }
}
