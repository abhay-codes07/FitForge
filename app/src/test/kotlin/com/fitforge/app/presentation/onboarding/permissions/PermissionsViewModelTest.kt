package com.fitforge.app.presentation.onboarding.permissions

import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.domain.usecase.onboarding.GetPermissionsDraftUseCase
import com.fitforge.app.domain.usecase.onboarding.SavePermissionsUseCase
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
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PermissionsViewModelTest {
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
    fun `loads saved permission states on init`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository(
            notificationState = UserPrefs.PermissionState.GRANTED,
            healthConnectState = UserPrefs.PermissionState.SKIPPED,
        )
        val viewModel = PermissionsViewModel(
            getPermissionsDraftUseCase = GetPermissionsDraftUseCase(repository),
            savePermissionsUseCase = SavePermissionsUseCase(repository),
        )

        advanceUntilIdle()

        assertEquals(UserPrefs.PermissionState.GRANTED, viewModel.uiState.value.notificationState)
        assertEquals(UserPrefs.PermissionState.SKIPPED, viewModel.uiState.value.healthConnectState)
        assertTrue(viewModel.uiState.value.canContinue)
    }

    @Test
    fun `marks health connect skipped when unavailable`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository()
        val viewModel = PermissionsViewModel(
            getPermissionsDraftUseCase = GetPermissionsDraftUseCase(repository),
            savePermissionsUseCase = SavePermissionsUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.setHealthConnectAvailability(false)

        assertFalse(viewModel.uiState.value.isHealthConnectAvailable)
        assertEquals(UserPrefs.PermissionState.SKIPPED, viewModel.uiState.value.healthConnectState)
    }

    @Test
    fun `saves permission states and routes to auth`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository()
        val viewModel = PermissionsViewModel(
            getPermissionsDraftUseCase = GetPermissionsDraftUseCase(repository),
            savePermissionsUseCase = SavePermissionsUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.onNotificationPermissionResult(true)
        viewModel.onHealthConnectPermissionResult(false)
        viewModel.onContinueClick()
        advanceUntilIdle()

        assertEquals(UserPrefs.PermissionState.GRANTED, repository.notificationState)
        assertEquals(UserPrefs.PermissionState.SKIPPED, repository.healthConnectState)
        assertEquals(Screen.Auth.route, viewModel.uiState.value.destinationRoute)
    }

    private class FakeOnboardingRepository(
        var notificationState: String = UserPrefs.PermissionState.PENDING,
        var healthConnectState: String = UserPrefs.PermissionState.PENDING,
    ) : OnboardingRepository {
        override suspend fun isOnboardingComplete(): Boolean = false
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
        override suspend fun getNotificationPermissionState(): String = notificationState
        override suspend fun setNotificationPermissionState(value: String) { notificationState = value }
        override suspend fun getHealthConnectPermissionState(): String = healthConnectState
        override suspend fun setHealthConnectPermissionState(value: String) { healthConnectState = value }
    }
}
