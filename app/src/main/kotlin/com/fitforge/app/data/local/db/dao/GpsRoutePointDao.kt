package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitforge.app.data.local.db.entity.GpsRoutePointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GpsRoutePointDao {
    @Query("SELECT * FROM gps_route_points WHERE workoutId = :workoutId ORDER BY timestampEpochMillis ASC, id ASC")
    fun observeRoutePoints(workoutId: String): Flow<List<GpsRoutePointEntity>>

    @Query("SELECT * FROM gps_route_points WHERE workoutId = :workoutId ORDER BY timestampEpochMillis ASC, id ASC")
    suspend fun getRoutePoints(workoutId: String): List<GpsRoutePointEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(point: GpsRoutePointEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<GpsRoutePointEntity>): List<Long>

    @Query("DELETE FROM gps_route_points WHERE workoutId = :workoutId")
    suspend fun deleteByWorkoutId(workoutId: String)

    @Delete
    suspend fun delete(point: GpsRoutePointEntity)
}
