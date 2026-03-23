package com.fitforge.app.presentation.onboarding.permissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.domain.usecase.onboarding.GetPermissionsDraftUseCase
import com.fitforge.app.domain.usecase.onboarding.SavePermissionsUseCase
import com.fitforge.app.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    private val getPermissionsDraftUseCase: GetPermissionsDraftUseCase,
    private val savePermissionsUseCase: SavePermissionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionsUiState())
    val uiState: StateFlow<PermissionsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val draft = getPermissionsDraftUseCase()
            _uiState.update {
                it.copy(
                    notificationState = draft.notificationState,
                    healthConnectState = draft.healthConnectState,
                )
            }
        }
    }

    fun setHealthConnectAvailability(isAvailable: Boolean) {
        _uiState.update { state ->
            state.copy(
                isHealthConnectAvailable = isAvailable,
                healthConnectState = if (!isAvailable && state.healthConnectState == UserPrefs.PermissionState.PENDING) {
                    UserPrefs.PermissionState.SKIPPED
                } else {
                    state.healthConnectState
                },
            )
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        _uiState.update {
            it.copy(
                notificationState = if (granted) UserPrefs.PermissionState.GRANTED else UserPrefs.PermissionState.SKIPPED,
            )
        }
    }

    fun onNotificationSkipped() {
        _uiState.update { it.copy(notificationState = UserPrefs.PermissionState.SKIPPED) }
    }

    fun onHealthConnectPermissionResult(granted: Boolean) {
        _uiState.update {
            it.copy(
                healthConnectState = if (granted) UserPrefs.PermissionState.GRANTED else UserPrefs.PermissionState.SKIPPED,
            )
        }
    }

    fun onHealthConnectSkipped() {
        _uiState.update { it.copy(healthConnectState = UserPrefs.PermissionState.SKIPPED) }
    }

    fun onContinueClick() {
        val current = uiState.value
        if (!current.canContinue) return

        viewModelScope.launch {
            savePermissionsUseCase(
                notificationState = current.notificationState,
                healthConnectState = current.healthConnectState,
            )
            _uiState.update { it.copy(destinationRoute = Screen.Auth.route) }
        }
    }
}
