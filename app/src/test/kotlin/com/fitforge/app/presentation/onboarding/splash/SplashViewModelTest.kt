package com.fitforge.app.presentation.onboarding.splash

import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.domain.usecase.onboarding.ResolveSplashDestinationUseCase
import com.fitforge.app.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

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
    fun `keeps destination empty before splash timeout`() = runTest(dispatcher) {
        val viewModel = SplashViewModel(
            resolveSplashDestinationUseCase = ResolveSplashDestinationUseCase(
                onboardingRepository = FakeOnboardingRepository(false),
            ),
        )

        advanceTimeBy(SplashViewModel.SPLASH_DELAY_MS - 1)
        runCurrent()

        assertNull(viewModel.uiState.value.destinationRoute)
    }

    @Test
    fun `routes to welcome after splash timeout when onboarding is incomplete`() = runTest(dispatcher) {
        val viewModel = SplashViewModel(
            resolveSplashDestinationUseCase = ResolveSplashDestinationUseCase(
                onboardingRepository = FakeOnboardingRepository(false),
            ),
        )

        advanceTimeBy(SplashViewModel.SPLASH_DELAY_MS)
        runCurrent()

        assertEquals(Screen.Welcome.route, viewModel.uiState.value.destinationRoute)
    }

    @Test
    fun `routes to home after splash timeout when onboarding is complete`() = runTest(dispatcher) {
        val viewModel = SplashViewModel(
            resolveSplashDestinationUseCase = ResolveSplashDestinationUseCase(
                onboardingRepository = FakeOnboardingRepository(true),
            ),
        )

        advanceTimeBy(SplashViewModel.SPLASH_DELAY_MS)
        runCurrent()

        assertEquals(Screen.Home.route, viewModel.uiState.value.destinationRoute)
    }

    private class FakeOnboardingRepository(
        private val isComplete: Boolean,
    ) : OnboardingRepository {
        override suspend fun isOnboardingComplete(): Boolean = isComplete
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
    }
}
