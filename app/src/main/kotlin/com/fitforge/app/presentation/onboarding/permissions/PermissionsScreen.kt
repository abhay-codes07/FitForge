package com.fitforge.app.presentation.onboarding.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.presentation.theme.Purple100
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Spacing

@Composable
fun PermissionsScreen(
    uiState: PermissionsUiState,
    onHealthConnectAvailabilityResolved: (Boolean) -> Unit,
    onNotificationPermissionResult: (Boolean) -> Unit,
    onNotificationSkipped: () -> Unit,
    onHealthConnectPermissionResult: (Boolean) -> Unit,
    onHealthConnectSkipped: () -> Unit,
    onContinue: () -> Unit,
    onNavigateNext: (String) -> Unit,
) {
    val context = LocalContext.current
    val healthPermissions = remember {
        setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
            HealthPermission.getWritePermission(ExerciseSessionRecord::class),
        )
    }

    LaunchedEffect(Unit) {
        val isAvailable = HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
        onHealthConnectAvailabilityResolved(isAvailable)
    }

    LaunchedEffect(uiState.destinationRoute) {
        uiState.destinationRoute?.let(onNavigateNext)
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onNotificationPermissionResult,
    )
    val healthConnectLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract(),
        onResult = { grantedPermissions ->
            onHealthConnectPermissionResult(grantedPermissions.containsAll(healthPermissions))
        },
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("permissions_screen"),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.xl),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                Text(
                    text = "Turn on the essentials",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Notifications keep your routine on track. Health Connect lets FitForge read activity data and write completed workouts.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                )

                PermissionCard(
                    title = "Notifications",
                    description = "Workout reminders, recovery nudges, and streak warnings.",
                    stateLabel = permissionLabel(uiState.notificationState),
                    primaryButtonLabel = if (uiState.notificationState == UserPrefs.PermissionState.GRANTED) "Enabled" else "Allow",
                    primaryTag = "request_notification_permission",
                    secondaryTag = "skip_notification_permission",
                    onPrimaryClick = {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                            onNotificationPermissionResult(true)
                        } else {
                            val granted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS,
                            ) == PackageManager.PERMISSION_GRANTED
                            if (granted) {
                                onNotificationPermissionResult(true)
                            } else {
                                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    },
                    onSecondaryClick = onNotificationSkipped,
                )

                PermissionCard(
                    title = "Health Connect",
                    description = if (uiState.isHealthConnectAvailable) {
                        "Sync steps, distance, calories, and workout sessions with Android Health Connect."
                    } else {
                        "Health Connect is not available on this device right now."
                    },
                    stateLabel = permissionLabel(uiState.healthConnectState),
                    primaryButtonLabel = if (uiState.isHealthConnectAvailable) "Connect" else "Unavailable",
                    primaryEnabled = uiState.isHealthConnectAvailable,
                    primaryTag = "request_health_connect_permission",
                    secondaryTag = "skip_health_connect_permission",
                    onPrimaryClick = { healthConnectLauncher.launch(healthPermissions) },
                    onSecondaryClick = onHealthConnectSkipped,
                )
            }

            Button(
                onClick = onContinue,
                enabled = uiState.canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("permissions_continue"),
                colors = ButtonDefaults.buttonColors(containerColor = Purple600),
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = Spacing.xxs),
                )
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    stateLabel: String,
    primaryButtonLabel: String,
    primaryTag: String,
    secondaryTag: String,
    primaryEnabled: Boolean = true,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Purple100,
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = Purple600,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                )
                Text(
                    text = stateLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = Purple600,
                )
            }
            Button(
                onClick = onPrimaryClick,
                enabled = primaryEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(primaryTag),
                colors = ButtonDefaults.buttonColors(containerColor = Purple600),
            ) {
                Text(primaryButtonLabel)
            }
            OutlinedButton(
                onClick = onSecondaryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(secondaryTag),
            ) {
                Text("Skip for now")
            }
        }
    }
}

private fun permissionLabel(state: String): String = when (state) {
    UserPrefs.PermissionState.GRANTED -> "Status: Enabled"
    UserPrefs.PermissionState.SKIPPED -> "Status: Skipped"
    else -> "Status: Pending"
}
