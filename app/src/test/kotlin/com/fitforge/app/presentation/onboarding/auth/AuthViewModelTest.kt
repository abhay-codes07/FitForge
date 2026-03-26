package com.fitforge.app.presentation.onboarding.auth

import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.domain.model.auth.AuthResult
import com.fitforge.app.domain.model.auth.AuthUser
import com.fitforge.app.domain.repository.AuthRepository
import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.domain.usecase.onboarding.CompleteOnboardingWithAuthUseCase
import com.fitforge.app.domain.usecase.onboarding.GetAuthDraftUseCase
import com.fitforge.app.domain.usecase.onboarding.SendPasswordResetUseCase
import com.fitforge.app.domain.usecase.onboarding.SignInAnonymouslyUseCase
import com.fitforge.app.domain.usecase.onboarding.SignInWithEmailUseCase
import com.fitforge.app.domain.usecase.onboarding.SignInWithGoogleUseCase
import com.fitforge.app.domain.usecase.onboarding.ValidateAuthCredentialsUseCase
import com.fitforge.app.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads saved auth draft on init`() = runTest(dispatcher) {
        val onboardingRepository = FakeOnboardingRepository(
            authMethod = UserPrefs.AuthMethod.GOOGLE,
            authEmail = "saved@example.com",
        )
        val authRepository = FakeAuthRepository()

        val viewModel = buildViewModel(onboardingRepository, authRepository)
        advanceUntilIdle()

        assertEquals(UserPrefs.AuthMethod.GOOGLE, viewModel.uiState.value.authMethod)
        assertEquals("saved@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun `shows validation errors for invalid email flow`() = runTest(dispatcher) {
        val viewModel = buildViewModel(FakeOnboardingRepository(), FakeAuthRepository())

        advanceUntilIdle()
        viewModel.onEmailChanged("bad")
        viewModel.onPasswordChanged("123")
        viewModel.onEmailContinueClick()

        assertTrue(viewModel.uiState.value.emailError != null)
        assertTrue(viewModel.uiState.value.passwordError != null)
    }

    @Test
    fun `completes onboarding as guest`() = runTest(dispatcher) {
        val onboardingRepository = FakeOnboardingRepository()
        val authRepository = FakeAuthRepository(
            anonymousResult = AuthResult.Success(
                AuthUser(uid = "guest", email = null, isAnonymous = true),
            ),
        )

        val viewModel = buildViewModel(onboardingRepository, authRepository)

        advanceUntilIdle()
        viewModel.onGuestContinueClick()
        advanceUntilIdle()

        assertEquals(UserPrefs.AuthMethod.ANONYMOUS, onboardingRepository.authMethod)
        assertEquals(true, onboardingRepository.onboardingComplete)
        assertEquals(Screen.Home.route, viewModel.uiState.value.destinationRoute)
    }

    @Test
    fun `sets error when google sign in fails`() = runTest(dispatcher) {
        val viewModel = buildViewModel(FakeOnboardingRepository(), FakeAuthRepository())

        advanceUntilIdle()
        viewModel.onGoogleSignInFailed("boom")

        assertEquals("boom", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `sends password reset with valid email`() = runTest(dispatcher) {
        val authRepository = FakeAuthRepository()
        val viewModel = buildViewModel(FakeOnboardingRepository(), authRepository)

        advanceUntilIdle()
        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordResetClick()
        advanceUntilIdle()

        assertEquals("user@example.com", authRepository.lastResetEmail)
        assertTrue(viewModel.uiState.value.infoMessage?.contains("Password reset link sent") == true)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    private fun buildViewModel(
        onboardingRepository: FakeOnboardingRepository,
        authRepository: FakeAuthRepository,
    ): AuthViewModel {
        return AuthViewModel(
            getAuthDraftUseCase = GetAuthDraftUseCase(onboardingRepository),
            validateAuthCredentialsUseCase = ValidateAuthCredentialsUseCase(),
            signInWithEmailUseCase = SignInWithEmailUseCase(authRepository),
            signInWithGoogleUseCase = SignInWithGoogleUseCase(authRepository),
            signInAnonymouslyUseCase = SignInAnonymouslyUseCase(authRepository),
            sendPasswordResetUseCase = SendPasswordResetUseCase(authRepository),
            completeOnboardingWithAuthUseCase = CompleteOnboardingWithAuthUseCase(onboardingRepository),
        )
    }

    private class FakeAuthRepository(
        private val emailResult: AuthResult = AuthResult.Success(
            AuthUser(uid = "email", email = "email@example.com", isAnonymous = false),
        ),
        private val googleResult: AuthResult = AuthResult.Success(
            AuthUser(uid = "google", email = "google@example.com", isAnonymous = false),
        ),
        private val anonymousResult: AuthResult = AuthResult.Success(
            AuthUser(uid = "anonymous", email = null, isAnonymous = true),
        ),
        private val resetResult: Result<Unit> = Result.success(Unit),
    ) : AuthRepository {
        var lastResetEmail: String? = null

        override suspend fun signInWithEmailPassword(email: String, password: String): AuthResult = emailResult

        override suspend fun signInWithGoogleIdToken(idToken: String): AuthResult = googleResult

        override suspend fun signInAnonymously(): AuthResult = anonymousResult

        override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
            lastResetEmail = email
            return resetResult
        }

        override suspend fun signOut() = Unit

        override fun getCurrentUser(): AuthUser? = null
    }

    private class FakeOnboardingRepository(
        var authMethod: String = UserPrefs.AuthMethod.NONE,
        var authEmail: String? = null,
        var onboardingComplete: Boolean = false,
    ) : OnboardingRepository {
        override suspend fun isOnboardingComplete(): Boolean = onboardingComplete
        override suspend fun setOnboardingComplete(completed: Boolean) { onboardingComplete = completed }
        override suspend fun getSelectedGoals(): Set<String> = emptySet()
        override suspend fun setSelectedGoals(values: Set<String>) = Unit
        override suspend fun getPreferredUnitSystem(): String = "metric"
        override suspend fun setPreferredUnitSystem(value: String) = Unit
        override suspend fun getHeightValue(): Float? = null
        override suspend fun setHeightValue(value: Float) = Unit
        override suspend fun getWeightValue(): Float? = null
        override suspend fun setWeightValue(value: Float) = Unit
        override suspend fun getAgeValue(): Float? = null
        override suspend fun setAgeValue(value: Float) = Unit
        override suspend fun getGenderValue(): String = "unspecified"
        override suspend fun setGenderValue(value: String) = Unit
        override suspend fun getFitnessLevel(): String = "unspecified"
        override suspend fun setFitnessLevel(value: String) = Unit
        override suspend fun getWorkoutLocations(): Set<String> = emptySet()
        override suspend fun setWorkoutLocations(values: Set<String>) = Unit
        override suspend fun getAvailableEquipment(): Set<String> = emptySet()
        override suspend fun setAvailableEquipment(values: Set<String>) = Unit
        override suspend fun getWorkoutDays(): Set<String> = emptySet()
        override suspend fun setWorkoutDays(values: Set<String>) = Unit
        override suspend fun getWorkoutDurationMinutes(): Int = 30
        override suspend fun setWorkoutDurationMinutes(value: Int) = Unit
        override suspend fun getNotificationPermissionState(): String = "pending"
        override suspend fun setNotificationPermissionState(value: String) = Unit
        override suspend fun getHealthConnectPermissionState(): String = "pending"
        override suspend fun setHealthConnectPermissionState(value: String) = Unit
        override suspend fun getAuthMethod(): String = authMethod
        override suspend fun setAuthMethod(value: String) { authMethod = value }
        override suspend fun getAuthEmail(): String? = authEmail
        override suspend fun setAuthEmail(value: String?) { authEmail = value }
    }
}
