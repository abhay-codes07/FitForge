package com.fitforge.app.presentation.onboarding.schedulesetup

import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.domain.usecase.onboarding.GetScheduleOptionsUseCase
import com.fitforge.app.domain.usecase.onboarding.GetScheduleSetupDraftUseCase
import com.fitforge.app.domain.usecase.onboarding.SaveScheduleSetupUseCase
import com.fitforge.app.domain.usecase.onboarding.ValidateScheduleSetupUseCase
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
class ScheduleSetupViewModelTest {
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
    fun `loads saved schedule draft on init`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository(
            workoutDays = setOf("mon", "wed", "fri"),
            workoutDurationMinutes = 45,
        )
        val viewModel = ScheduleSetupViewModel(
            getScheduleOptionsUseCase = GetScheduleOptionsUseCase(),
            getScheduleSetupDraftUseCase = GetScheduleSetupDraftUseCase(repository),
            validateScheduleSetupUseCase = ValidateScheduleSetupUseCase(),
            saveScheduleSetupUseCase = SaveScheduleSetupUseCase(repository),
        )

        advanceUntilIdle()

        assertEquals(setOf("mon", "wed", "fri"), viewModel.uiState.value.selectedDays)
        assertEquals(45, viewModel.uiState.value.durationMinutes)
        assertEquals(7, viewModel.uiState.value.dayOptions.size)
    }

    @Test
    fun `shows error when continuing without selected days`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository()
        val viewModel = ScheduleSetupViewModel(
            getScheduleOptionsUseCase = GetScheduleOptionsUseCase(),
            getScheduleSetupDraftUseCase = GetScheduleSetupDraftUseCase(repository),
            validateScheduleSetupUseCase = ValidateScheduleSetupUseCase(),
            saveScheduleSetupUseCase = SaveScheduleSetupUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.onContinueClick()

        assertTrue(viewModel.uiState.value.dayError != null)
    }

    @Test
    fun `saves schedule and routes to permissions`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository()
        val viewModel = ScheduleSetupViewModel(
            getScheduleOptionsUseCase = GetScheduleOptionsUseCase(),
            getScheduleSetupDraftUseCase = GetScheduleSetupDraftUseCase(repository),
            validateScheduleSetupUseCase = ValidateScheduleSetupUseCase(),
            saveScheduleSetupUseCase = SaveScheduleSetupUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.onDayToggle("tue")
        viewModel.onDayToggle("thu")
        viewModel.onDurationChanged(60f)
        viewModel.onContinueClick()
        advanceUntilIdle()

        assertEquals(setOf("tue", "thu"), repository.workoutDays)
        assertEquals(60, repository.workoutDurationMinutes)
        assertEquals(Screen.Permissions.route, viewModel.uiState.value.destinationRoute)
    }

    private class FakeOnboardingRepository(
        var workoutDays: Set<String> = emptySet(),
        var workoutDurationMinutes: Int = 30,
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
        override suspend fun getWorkoutDays(): Set<String> = workoutDays
        override suspend fun setWorkoutDays(values: Set<String>) { workoutDays = values }
        override suspend fun getWorkoutDurationMinutes(): Int = workoutDurationMinutes
        override suspend fun setWorkoutDurationMinutes(value: Int) { workoutDurationMinutes = value }
        override suspend fun getNotificationPermissionState(): String = "pending"
        override suspend fun setNotificationPermissionState(value: String) = Unit
        override suspend fun getHealthConnectPermissionState(): String = "pending"
        override suspend fun setHealthConnectPermissionState(value: String) = Unit
    }
}
