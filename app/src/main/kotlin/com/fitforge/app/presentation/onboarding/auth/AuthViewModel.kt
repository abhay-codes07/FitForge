package com.fitforge.app.presentation.onboarding.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.domain.usecase.onboarding.CompleteOnboardingWithAuthUseCase
import com.fitforge.app.domain.usecase.onboarding.GetAuthDraftUseCase
import com.fitforge.app.domain.usecase.onboarding.ValidateAuthCredentialsUseCase
import com.fitforge.app.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAuthDraftUseCase: GetAuthDraftUseCase,
    private val validateAuthCredentialsUseCase: ValidateAuthCredentialsUseCase,
    private val completeOnboardingWithAuthUseCase: CompleteOnboardingWithAuthUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val draft = getAuthDraftUseCase()
            _uiState.update {
                it.copy(
                    email = draft.email,
                    authMethod = draft.authMethod,
                )
            }
        }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onEmailContinueClick() {
        val current = uiState.value
        val validation = validateAuthCredentialsUseCase(current.email, current.password)
        if (!validation.isValid) {
            _uiState.update {
                it.copy(
                    emailError = validation.emailError,
                    passwordError = validation.passwordError,
                )
            }
            return
        }

        complete(UserPrefs.AuthMethod.EMAIL, current.email.trim())
    }

    fun onGoogleContinueClick() {
        val email = uiState.value.email.trim().takeIf { it.isNotBlank() }
        complete(UserPrefs.AuthMethod.GOOGLE, email)
    }

    fun onGuestContinueClick() {
        complete(UserPrefs.AuthMethod.ANONYMOUS, null)
    }

    private fun complete(authMethod: String, email: String?) {
        viewModelScope.launch {
            completeOnboardingWithAuthUseCase(authMethod = authMethod, email = email)
            _uiState.update {
                it.copy(
                    authMethod = authMethod,
                    destinationRoute = Screen.Home.route,
                )
            }
        }
    }
}
