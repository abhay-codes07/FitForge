package com.fitforge.app.presentation.onboarding.workoutpreferences

import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.domain.usecase.onboarding.GetWorkoutPreferencesDraftUseCase
import com.fitforge.app.domain.usecase.onboarding.GetWorkoutPreferencesOptionsUseCase
import com.fitforge.app.domain.usecase.onboarding.SaveWorkoutPreferencesUseCase
import com.fitforge.app.domain.usecase.onboarding.ValidateWorkoutPreferencesUseCase
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
class WorkoutPreferencesViewModelTest {
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
    fun `loads saved draft on init`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository(
            workoutLocations = setOf(UserPrefs.WorkoutLocation.GYM),
            availableEquipment = setOf(UserPrefs.Equipment.BARBELL, UserPrefs.Equipment.BENCH),
        )
        val viewModel = WorkoutPreferencesViewModel(
            getWorkoutPreferencesOptionsUseCase = GetWorkoutPreferencesOptionsUseCase(),
            getWorkoutPreferencesDraftUseCase = GetWorkoutPreferencesDraftUseCase(repository),
            validateWorkoutPreferencesUseCase = ValidateWorkoutPreferencesUseCase(),
            saveWorkoutPreferencesUseCase = SaveWorkoutPreferencesUseCase(repository),
        )

        advanceUntilIdle()

        assertEquals(setOf(UserPrefs.WorkoutLocation.GYM), viewModel.uiState.value.selectedLocations)
        assertEquals(setOf(UserPrefs.Equipment.BARBELL, UserPrefs.Equipment.BENCH), viewModel.uiState.value.selectedEquipment)
        assertEquals(3, viewModel.uiState.value.locationOptions.size)
    }

    @Test
    fun `shows validation error when continuing without a location`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository()
        val viewModel = WorkoutPreferencesViewModel(
            getWorkoutPreferencesOptionsUseCase = GetWorkoutPreferencesOptionsUseCase(),
            getWorkoutPreferencesDraftUseCase = GetWorkoutPreferencesDraftUseCase(repository),
            validateWorkoutPreferencesUseCase = ValidateWorkoutPreferencesUseCase(),
            saveWorkoutPreferencesUseCase = SaveWorkoutPreferencesUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.onContinueClick()

        assertTrue(viewModel.uiState.value.locationError != null)
    }

    @Test
    fun `saves selection and routes to schedule setup`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository()
        val viewModel = WorkoutPreferencesViewModel(
            getWorkoutPreferencesOptionsUseCase = GetWorkoutPreferencesOptionsUseCase(),
            getWorkoutPreferencesDraftUseCase = GetWorkoutPreferencesDraftUseCase(repository),
            validateWorkoutPreferencesUseCase = ValidateWorkoutPreferencesUseCase(),
            saveWorkoutPreferencesUseCase = SaveWorkoutPreferencesUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.onLocationToggle(UserPrefs.WorkoutLocation.HOME)
        viewModel.onEquipmentToggle(UserPrefs.Equipment.DUMBBELLS)
        viewModel.onContinueClick()
        advanceUntilIdle()

        assertEquals(setOf(UserPrefs.WorkoutLocation.HOME), repository.workoutLocations)
        assertEquals(setOf(UserPrefs.Equipment.DUMBBELLS), repository.availableEquipment)
        assertEquals(Screen.ScheduleSetup.route, viewModel.uiState.value.destinationRoute)
    }

    private class FakeOnboardingRepository(
        var workoutLocations: Set<String> = emptySet(),
        var availableEquipment: Set<String> = emptySet(),
    ) : OnboardingRepository {
        override suspend fun isOnboardingComplete(): Boolean = false
        override suspend fun setOnboardingComplete(completed: Boolean) = Unit
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
        override suspend fun getWorkoutLocations(): Set<String> = workoutLocations
        override suspend fun setWorkoutLocations(values: Set<String>) { workoutLocations = values }
        override suspend fun getAvailableEquipment(): Set<String> = availableEquipment
        override suspend fun setAvailableEquipment(values: Set<String>) { availableEquipment = values }
        override suspend fun getWorkoutDays(): Set<String> = emptySet()
        override suspend fun setWorkoutDays(values: Set<String>) = Unit
        override suspend fun getWorkoutDurationMinutes(): Int = 30
        override suspend fun setWorkoutDurationMinutes(value: Int) = Unit
        override suspend fun getNotificationPermissionState(): String = "pending"
        override suspend fun setNotificationPermissionState(value: String) = Unit
        override suspend fun getHealthConnectPermissionState(): String = "pending"
        override suspend fun setHealthConnectPermissionState(value: String) = Unit
        override suspend fun getAuthMethod(): String = "none"
        override suspend fun setAuthMethod(value: String) = Unit
        override suspend fun getAuthEmail(): String? = null
        override suspend fun setAuthEmail(value: String?) = Unit
    }
}




