package com.fitforge.app.presentation.onboarding.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.domain.model.auth.AuthResult
import com.fitforge.app.domain.usecase.onboarding.CompleteOnboardingWithAuthUseCase
import com.fitforge.app.domain.usecase.onboarding.GetAuthDraftUseCase
import com.fitforge.app.domain.usecase.onboarding.SendPasswordResetUseCase
import com.fitforge.app.domain.usecase.onboarding.SignInAnonymouslyUseCase
import com.fitforge.app.domain.usecase.onboarding.SignInWithEmailUseCase
import com.fitforge.app.domain.usecase.onboarding.SignInWithGoogleUseCase
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
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase,
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
        _uiState.update { it.copy(email = value, emailError = null, errorMessage = null, infoMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, errorMessage = null, infoMessage = null) }
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

        runAuthAction {
            when (val result = signInWithEmailUseCase(current.email, current.password)) {
                is AuthResult.Success -> {
                    complete(
                        authMethod = UserPrefs.AuthMethod.EMAIL,
                        email = result.user.email ?: current.email.trim(),
                    )
                }

                is AuthResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun onGoogleIdTokenReceived(idToken: String) {
        if (idToken.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Google Sign-In failed. Please try again.") }
            return
        }

        runAuthAction {
            when (val result = signInWithGoogleUseCase(idToken)) {
                is AuthResult.Success -> {
                    complete(
                        authMethod = UserPrefs.AuthMethod.GOOGLE,
                        email = result.user.email,
                    )
                }

                is AuthResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun onGoogleSignInFailed(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = if (message.isBlank()) "Google Sign-In failed. Please try again." else message,
            )
        }
    }

    fun onGuestContinueClick() {
        runAuthAction {
            when (val result = signInAnonymouslyUseCase()) {
                is AuthResult.Success -> {
                    complete(
                        authMethod = UserPrefs.AuthMethod.ANONYMOUS,
                        email = result.user.email,
                    )
                }

                is AuthResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun onPasswordResetClick() {
        val email = uiState.value.email
        val emailError = validateAuthCredentialsUseCase.validateEmail(email)
        if (emailError != null) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    errorMessage = null,
                    infoMessage = null,
                )
            }
            return
        }

        runAuthAction {
            sendPasswordResetUseCase(email)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            infoMessage = "Password reset link sent to ${email.trim()}.",
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Failed to send password reset email.",
                            infoMessage = null,
                        )
                    }
                }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(destinationRoute = null) }
    }

    private fun runAuthAction(action: suspend () -> Unit) {
        if (uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            action()
        }
    }

    private suspend fun complete(authMethod: String, email: String?) {
        completeOnboardingWithAuthUseCase(authMethod = authMethod, email = email)
        _uiState.update {
            it.copy(
                isLoading = false,
                authMethod = authMethod,
                destinationRoute = Screen.Home.route,
            )
        }
    }
}
