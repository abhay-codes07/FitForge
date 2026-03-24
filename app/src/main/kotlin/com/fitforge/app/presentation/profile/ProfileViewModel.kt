package com.fitforge.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.profile.ObserveProfileSettingsUseCase
import com.fitforge.app.domain.usecase.profile.UpdateThemePreferenceUseCase
import com.fitforge.app.domain.usecase.profile.UpdateUnitSystemPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val observeProfileSettingsUseCase: ObserveProfileSettingsUseCase,
    private val updateThemePreferenceUseCase: UpdateThemePreferenceUseCase,
    private val updateUnitSystemPreferenceUseCase: UpdateUnitSystemPreferenceUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeProfile()
    }

    fun onThemeSelected(theme: String) {
        viewModelScope.launch {
            runCatching { updateThemePreferenceUseCase(theme) }
                .onFailure { throwable ->
                    _uiState.update { it.copy(errorMessage = throwable.message ?: "Unable to update theme") }
                }
        }
    }

    fun onUnitSystemSelected(unit: String) {
        viewModelScope.launch {
            runCatching { updateUnitSystemPreferenceUseCase(unit) }
                .onFailure { throwable ->
                    _uiState.update { it.copy(errorMessage = throwable.message ?: "Unable to update unit system") }
                }
        }
    }

    private fun observeProfile() {
        viewModelScope.launch {
            observeProfileSettingsUseCase()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unable to load profile",
                        )
                    }
                }
                .collect { profile ->
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        displayName = profile.displayName,
                        email = profile.email ?: "Not linked",
                        fitnessLevel = profile.fitnessLevel,
                        goals = profile.goals.toList().sorted(),
                        themePreference = profile.themePreference,
                        preferredUnitSystem = profile.preferredUnitSystem,
                        notificationsEnabled = profile.notificationsEnabled,
                    )
                }
        }
    }
}
