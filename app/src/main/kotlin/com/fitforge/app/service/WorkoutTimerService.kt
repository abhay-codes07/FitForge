package com.fitforge.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.fitforge.app.R
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.max
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkoutTimerService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private var timerJob: Job? = null
    private var remainingSeconds: Int = 0
    private var isRunning: Boolean = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val durationSeconds = intent.getIntExtra(EXTRA_DURATION_SECONDS, 0)
                startTimer(max(durationSeconds, 0))
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_STOP -> stopTimerService()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timerJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startTimer(durationSeconds: Int) {
        timerJob?.cancel()
        remainingSeconds = durationSeconds
        isRunning = true

        startForeground(NOTIFICATION_ID, buildNotification())
        startTicking()
    }

    private fun pauseTimer() {
        if (!isRunning) return
        isRunning = false
        timerJob?.cancel()
        updateNotification()
    }

    private fun resumeTimer() {
        if (isRunning || remainingSeconds <= 0) return
        isRunning = true
        updateNotification()
        startTicking()
    }

    private fun startTicking() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isRunning && remainingSeconds > 0) {
                delay(1000)
                remainingSeconds -= 1
                updateNotification()
            }
            if (remainingSeconds <= 0) {
                stopTimerService()
            }
        }
    }

    private fun stopTimerService() {
        timerJob?.cancel()
        isRunning = false
        remainingSeconds = 0
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val pauseOrResumeAction = if (isRunning) {
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
            .setContentTitle("Workout timer")
            .setContentText(WorkoutTimerFormatter.formatRemainingTime(remainingSeconds))
            .setOnlyAlertOnce(true)
            .setOngoing(isRunning)
            .addAction(pauseOrResumeAction)
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
            "Workout Timer",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Shows active workout timer controls"
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "workout_timer"
        private const val NOTIFICATION_ID = 4201

        const val ACTION_START = "com.fitforge.app.service.action.START"
        const val ACTION_PAUSE = "com.fitforge.app.service.action.PAUSE"
        const val ACTION_RESUME = "com.fitforge.app.service.action.RESUME"
        const val ACTION_STOP = "com.fitforge.app.service.action.STOP"
        const val EXTRA_DURATION_SECONDS = "duration_seconds"

        fun start(context: Context, durationSeconds: Int) {
            val intent = Intent(context, WorkoutTimerService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_DURATION_SECONDS, durationSeconds)
            }
            context.startForegroundServiceCompat(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, WorkoutTimerService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        private fun servicePendingIntent(
            context: Context,
            action: String,
        ): PendingIntent {
            val intent = Intent(context, WorkoutTimerService::class.java).apply {
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

