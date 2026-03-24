package com.fitforge.app.presentation.onboarding.auth

import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.domain.usecase.onboarding.CompleteOnboardingWithAuthUseCase
import com.fitforge.app.domain.usecase.onboarding.GetAuthDraftUseCase
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
        val repository = FakeOnboardingRepository(
            authMethod = UserPrefs.AuthMethod.GOOGLE,
            authEmail = "saved@example.com",
        )
        val viewModel = AuthViewModel(
            getAuthDraftUseCase = GetAuthDraftUseCase(repository),
            validateAuthCredentialsUseCase = ValidateAuthCredentialsUseCase(),
            completeOnboardingWithAuthUseCase = CompleteOnboardingWithAuthUseCase(repository),
        )

        advanceUntilIdle()

        assertEquals(UserPrefs.AuthMethod.GOOGLE, viewModel.uiState.value.authMethod)
        assertEquals("saved@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun `shows validation errors for invalid email flow`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository()
        val viewModel = AuthViewModel(
            getAuthDraftUseCase = GetAuthDraftUseCase(repository),
            validateAuthCredentialsUseCase = ValidateAuthCredentialsUseCase(),
            completeOnboardingWithAuthUseCase = CompleteOnboardingWithAuthUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.onEmailChanged("bad")
        viewModel.onPasswordChanged("123")
        viewModel.onEmailContinueClick()

        assertTrue(viewModel.uiState.value.emailError != null)
        assertTrue(viewModel.uiState.value.passwordError != null)
    }

    @Test
    fun `completes onboarding as guest`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository()
        val viewModel = AuthViewModel(
            getAuthDraftUseCase = GetAuthDraftUseCase(repository),
            validateAuthCredentialsUseCase = ValidateAuthCredentialsUseCase(),
            completeOnboardingWithAuthUseCase = CompleteOnboardingWithAuthUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.onGuestContinueClick()
        advanceUntilIdle()

        assertEquals(UserPrefs.AuthMethod.ANONYMOUS, repository.authMethod)
        assertEquals(true, repository.onboardingComplete)
        assertEquals(Screen.Home.route, viewModel.uiState.value.destinationRoute)
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
