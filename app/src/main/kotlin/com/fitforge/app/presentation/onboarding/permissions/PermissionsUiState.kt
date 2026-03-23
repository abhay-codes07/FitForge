package com.fitforge.app.presentation.onboarding.permissions

import com.fitforge.app.data.local.datastore.UserPrefs

data class PermissionsUiState(
    val notificationState: String = UserPrefs.PermissionState.PENDING,
    val healthConnectState: String = UserPrefs.PermissionState.PENDING,
    val isHealthConnectAvailable: Boolean = true,
    val destinationRoute: String? = null,
) {
    val canContinue: Boolean
        get() = notificationState != UserPrefs.PermissionState.PENDING &&
            healthConnectState != UserPrefs.PermissionState.PENDING
}
