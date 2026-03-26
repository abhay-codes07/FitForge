package com.fitforge.app.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.fitforge.app.R
import com.fitforge.app.data.local.db.entity.GpsRoutePointEntity
import com.fitforge.app.domain.usecase.gps_tracking.RecordGpsRoutePointUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationTrackingService : Service() {
    @Inject
    lateinit var recordGpsRoutePointUseCase: RecordGpsRoutePointUseCase

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private var elapsedTickerJob: Job? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    private var workoutId: String? = null
    private var mode: String = "Run"
    private var segmentIndex: Int = 0
    private var isTracking: Boolean = false
    private var elapsedSeconds: Long = 0L
    private var lastDistancePoint: Location? = null
    private var totalDistanceMeters: Float = 0f

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                workoutId = intent.getStringExtra(EXTRA_WORKOUT_ID)
                mode = intent.getStringExtra(EXTRA_MODE) ?: "Run"
                startTracking()
            }
            ACTION_PAUSE -> pauseTracking()
            ACTION_RESUME -> resumeTracking()
            ACTION_STOP -> stopTracking(finalize = true)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopLocationUpdates()
        elapsedTickerJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startTracking() {
        if (workoutId.isNullOrBlank()) {
            stopSelf()
            return
        }
        if (!hasLocationPermission()) {
            stopSelf()
            return
        }

        elapsedSeconds = 0L
        totalDistanceMeters = 0f
        lastDistancePoint = null
        segmentIndex = 0
        isTracking = true

        startForeground(NOTIFICATION_ID, buildNotification())
        startElapsedTicker()
        startLocationUpdates()
    }

    private fun pauseTracking() {
        if (!isTracking) return
        isTracking = false
        stopLocationUpdates()
        elapsedTickerJob?.cancel()
        updateNotification()
    }

    private fun resumeTracking() {
        if (isTracking || workoutId.isNullOrBlank()) return
        if (!hasLocationPermission()) return

        segmentIndex += 1
        isTracking = true
        startElapsedTicker()
        startLocationUpdates()
        updateNotification()
    }

    private fun stopTracking(finalize: Boolean) {
        isTracking = false
        stopLocationUpdates()
        elapsedTickerJob?.cancel()
        if (finalize) {
            val stopIntent = Intent(ACTION_GPS_TRACKING_STOPPED).apply {
                setPackage(packageName)
                putExtra(EXTRA_WORKOUT_ID, workoutId)
                putExtra(EXTRA_DURATION_SECONDS, elapsedSeconds)
                putExtra(EXTRA_DISTANCE_METERS, totalDistanceMeters)
            }
            sendBroadcast(stopIntent)
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startElapsedTicker() {
        elapsedTickerJob?.cancel()
        elapsedTickerJob = serviceScope.launch {
            while (isTracking) {
                delay(1000)
                elapsedSeconds += 1
                updateNotification()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3_000L)
            .setMinUpdateIntervalMillis(2_000L)
            .setMinUpdateDistanceMeters(5f)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                if (!isTracking || workoutId.isNullOrBlank()) return

                result.locations.forEach { location ->
                    val previous = lastDistancePoint
                    if (previous != null) {
                        totalDistanceMeters += previous.distanceTo(location)
                    }
                    lastDistancePoint = location

                    val routePoint = GpsRoutePointEntity(
                        workoutId = workoutId.orEmpty(),
                        latitude = location.latitude,
                        longitude = location.longitude,
                        altitudeMeters = if (location.hasAltitude()) location.altitude else null,
                        accuracyMeters = if (location.hasAccuracy()) location.accuracy else null,
                        speedMetersPerSecond = if (location.hasSpeed()) location.speed else null,
                        heartRate = null,
                        timestampEpochMillis = System.currentTimeMillis(),
                        segmentIndex = segmentIndex,
                    )

                    serviceScope.launch {
                        recordGpsRoutePointUseCase(routePoint)
                    }
                }
            }
        }

        locationCallback = callback
        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
        }
        locationCallback = null
    }

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val action = if (isTracking) {
            NotificationCompat.Action(
                0,
                "Pause",
                servicePendingIntent(this, ACTION_PAUSE),
            )
        } else {
            NotificationCompat.Action(
                0,
                "Resume",
                servicePendingIntent(this, ACTION_RESUME),
            )
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_fitforge_mark)
            .setContentTitle("$mode tracking")
            .setContentText("${formatDuration(elapsedSeconds)} • %.2f km".format(totalDistanceMeters / 1000f))
            .setOnlyAlertOnce(true)
            .setOngoing(isTracking)
            .addAction(action)
            .addAction(
                NotificationCompat.Action(
                    0,
                    "Stop",
                    servicePendingIntent(this, ACTION_STOP),
                ),
            )
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "GPS Tracking",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Tracks active run, walk, and cycle workouts"
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    private fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }

    companion object {
        private const val CHANNEL_ID = "gps_tracking"
        private const val NOTIFICATION_ID = 4301

        const val ACTION_START = "com.fitforge.app.service.gps.START"
        const val ACTION_PAUSE = "com.fitforge.app.service.gps.PAUSE"
        const val ACTION_RESUME = "com.fitforge.app.service.gps.RESUME"
        const val ACTION_STOP = "com.fitforge.app.service.gps.STOP"

        const val ACTION_GPS_TRACKING_STOPPED = "com.fitforge.app.service.gps.STOPPED"

        const val EXTRA_WORKOUT_ID = "extra_workout_id"
        const val EXTRA_MODE = "extra_mode"
        const val EXTRA_DURATION_SECONDS = "extra_duration_seconds"
        const val EXTRA_DISTANCE_METERS = "extra_distance_meters"

        fun start(context: Context, workoutId: String, mode: String) {
            val intent = Intent(context, LocationTrackingService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_WORKOUT_ID, workoutId)
                putExtra(EXTRA_MODE, mode)
            }
            context.startForegroundServiceCompat(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, LocationTrackingService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        private fun servicePendingIntent(
            context: Context,
            action: String,
        ): PendingIntent {
            val intent = Intent(context, LocationTrackingService::class.java).apply {
                this.action = action
            }
            return PendingIntent.getService(
                context,
                action.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        private fun Context.startForegroundServiceCompat(intent: Intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }
}
