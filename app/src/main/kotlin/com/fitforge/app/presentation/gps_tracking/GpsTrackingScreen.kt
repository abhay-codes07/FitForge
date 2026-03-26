package com.fitforge.app.presentation.gps_tracking

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.fitforge.app.data.local.db.entity.GpsRoutePointEntity
import com.fitforge.app.presentation.theme.DarkSurface3
import com.fitforge.app.presentation.theme.Mint500
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Radius
import com.fitforge.app.presentation.theme.Spacing
import com.fitforge.app.service.LocationTrackingService
import java.util.Locale

@Composable
fun GpsTrackingScreen(
    uiState: GpsTrackingUiState,
    onModeSelected: (String) -> Unit,
    onStartTracking: () -> Unit,
    onStopTracking: () -> Unit,
    onServiceStartHandled: () -> Unit,
    onServiceStopHandled: () -> Unit,
    onServiceStopped: (Long, Float) -> Unit,
    onDismissError: () -> Unit,
) {
    val context = LocalContext.current

    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        hasLocationPermission = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action != LocationTrackingService.ACTION_GPS_TRACKING_STOPPED) return
                val duration = intent.getLongExtra(LocationTrackingService.EXTRA_DURATION_SECONDS, uiState.elapsedSeconds)
                val distance = intent.getFloatExtra(LocationTrackingService.EXTRA_DISTANCE_METERS, uiState.distanceMeters)
                onServiceStopped(duration, distance)
            }
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(LocationTrackingService.ACTION_GPS_TRACKING_STOPPED),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    LaunchedEffect(uiState.pendingStartRequest) {
        val request = uiState.pendingStartRequest ?: return@LaunchedEffect
        if (!hasLocationPermission) {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            LocationTrackingService.start(context, request.workoutId, request.mode)
            onServiceStartHandled()
        }
    }

    LaunchedEffect(uiState.pendingStopRequest) {
        if (!uiState.pendingStopRequest) return@LaunchedEffect
        LocationTrackingService.stop(context)
        onServiceStopHandled()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Spacing.md)
            .testTag("gps_tracking_screen"),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = "Run / Walk / Cycle",
            style = MaterialTheme.typography.headlineMedium,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            uiState.availableModes.forEach { mode ->
                val selected = mode == uiState.selectedMode
                Button(
                    onClick = { onModeSelected(mode) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected) Purple600 else DarkSurface3,
                    ),
                    modifier = Modifier.testTag("gps_mode_$mode"),
                ) {
                    Text(mode)
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .testTag("gps_route_map"),
            shape = RoundedCornerShape(Radius.lg),
        ) {
            RouteCanvas(points = uiState.routePoints)
        }

        GpsStatRow(
            leftLabel = "Distance",
            leftValue = "%.2f km".format(uiState.distanceMeters / 1000f),
            rightLabel = "Duration",
            rightValue = formatDuration(uiState.elapsedSeconds),
        )
        GpsStatRow(
            leftLabel = "Speed",
            leftValue = "%.2f m/s".format(uiState.currentSpeedMetersPerSecond),
            rightLabel = "Pace",
            rightValue = if (uiState.averagePaceMinPerKm > 0f) {
                "%.2f min/km".format(uiState.averagePaceMinPerKm)
            } else {
                "-"
            },
        )

        if (uiState.errorMessage != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("gps_error"),
                )
                Button(onClick = onDismissError) { Text("Dismiss") }
            }
        }

        if (!hasLocationPermission) {
            Button(
                onClick = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                        ),
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gps_permission_button"),
            ) {
                Text("Grant Location Permission")
            }
        }

        Button(
            onClick = {
                if (uiState.isTracking) onStopTracking() else onStartTracking()
            },
            enabled = hasLocationPermission,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("gps_toggle_tracking"),
            colors = ButtonDefaults.buttonColors(containerColor = Mint500),
        ) {
            Text(if (uiState.isTracking) "Stop Tracking" else "Start ${uiState.selectedMode}")
        }
    }
}

@Composable
private fun RouteCanvas(points: List<GpsRoutePointEntity>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1220)),
    ) {
        if (points.size < 2) {
            Text(
                text = "Map route will appear here",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.Center),
            )
            return
        }

        val minLat = points.minOf { it.latitude }
        val maxLat = points.maxOf { it.latitude }
        val minLon = points.minOf { it.longitude }
        val maxLon = points.maxOf { it.longitude }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
        ) {
            val width = size.width
            val height = size.height

            fun project(point: GpsRoutePointEntity): Offset {
                val lonRange = (maxLon - minLon).takeIf { it != 0.0 } ?: 0.0001
                val latRange = (maxLat - minLat).takeIf { it != 0.0 } ?: 0.0001
                val x = ((point.longitude - minLon) / lonRange).toFloat() * width
                val y = height - (((point.latitude - minLat) / latRange).toFloat() * height)
                return Offset(x, y)
            }

            for (i in 1 until points.size) {
                drawLine(
                    color = Mint500,
                    start = project(points[i - 1]),
                    end = project(points[i]),
                    strokeWidth = 6f,
                )
            }

            drawCircle(
                color = Purple600,
                radius = 8f,
                center = project(points.first()),
                style = Stroke(width = 3f),
            )
            drawCircle(
                color = Mint500,
                radius = 8f,
                center = project(points.last()),
            )
        }
    }
}

@Composable
private fun GpsStatRow(
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = leftLabel,
            value = leftValue,
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = rightLabel,
            value = rightValue,
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    label: String,
    value: String,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            Text(text = value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, secs)
    }
}

private fun Context.hasLocationPermission(): Boolean {
    val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    return fine || coarse
}

