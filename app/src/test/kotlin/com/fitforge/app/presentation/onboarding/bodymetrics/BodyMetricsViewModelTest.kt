package com.fitforge.app.presentation.onboarding.bodymetrics

import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.domain.usecase.onboarding.GetBodyMetricsDraftUseCase
import com.fitforge.app.domain.usecase.onboarding.SaveBodyMetricsUseCase
import com.fitforge.app.domain.usecase.onboarding.ValidateBodyMetricsUseCase
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
class BodyMetricsViewModelTest {
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
    fun `loads persisted draft on init`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository(
            unitSystem = BodyMetricsUnitSystem.Imperial.storageValue,
            height = 70f,
            weight = 180f,
            age = 31f,
            gender = BodyMetricsGender.Male.storageValue,
        )
        val viewModel = BodyMetricsViewModel(
            getBodyMetricsDraftUseCase = GetBodyMetricsDraftUseCase(repository),
            validateBodyMetricsUseCase = ValidateBodyMetricsUseCase(),
            saveBodyMetricsUseCase = SaveBodyMetricsUseCase(repository),
        )

        advanceUntilIdle()

        assertEquals(BodyMetricsUnitSystem.Imperial, viewModel.uiState.value.unitSystem)
        assertEquals("70", viewModel.uiState.value.height)
        assertEquals("180", viewModel.uiState.value.weight)
        assertEquals("31", viewModel.uiState.value.age)
        assertEquals(BodyMetricsGender.Male, viewModel.uiState.value.gender)
    }

    @Test
    fun `shows validation errors for invalid values`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository()
        val viewModel = BodyMetricsViewModel(
            getBodyMetricsDraftUseCase = GetBodyMetricsDraftUseCase(repository),
            validateBodyMetricsUseCase = ValidateBodyMetricsUseCase(),
            saveBodyMetricsUseCase = SaveBodyMetricsUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.onHeightChanged("20")
        viewModel.onWeightChanged("1")
        viewModel.onAgeChanged("8")
        viewModel.onContinueClick()

        assertTrue(viewModel.uiState.value.heightError != null)
        assertTrue(viewModel.uiState.value.weightError != null)
        assertTrue(viewModel.uiState.value.ageError != null)
    }

    @Test
    fun `saves valid metrics and routes to fitness level`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository()
        val viewModel = BodyMetricsViewModel(
            getBodyMetricsDraftUseCase = GetBodyMetricsDraftUseCase(repository),
            validateBodyMetricsUseCase = ValidateBodyMetricsUseCase(),
            saveBodyMetricsUseCase = SaveBodyMetricsUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.onUnitSystemSelected(BodyMetricsUnitSystem.Metric)
        viewModel.onHeightChanged("175")
        viewModel.onWeightChanged("74")
        viewModel.onAgeChanged("29")
        viewModel.onGenderSelected(BodyMetricsGender.Female)
        viewModel.onContinueClick()
        advanceUntilIdle()

        assertEquals(175f, repository.height)
        assertEquals(74f, repository.weight)
        assertEquals(29f, repository.age)
        assertEquals(BodyMetricsGender.Female.storageValue, repository.gender)
        assertEquals(Screen.FitnessLevel.route, viewModel.uiState.value.destinationRoute)
    }

    private class FakeOnboardingRepository(
        private var unitSystem: String = BodyMetricsUnitSystem.Metric.storageValue,
        var height: Float? = null,
        var weight: Float? = null,
        var age: Float? = null,
        var gender: String = BodyMetricsGender.Unspecified.storageValue,
    ) : OnboardingRepository {
        override suspend fun isOnboardingComplete(): Boolean = false
        override suspend fun setOnboardingComplete(completed: Boolean) = Unit
        override suspend fun getSelectedGoals(): Set<String> = emptySet()
        override suspend fun setSelectedGoals(values: Set<String>) = Unit
        override suspend fun getPreferredUnitSystem(): String = unitSystem
        override suspend fun setPreferredUnitSystem(value: String) { unitSystem = value }
        override suspend fun getHeightValue(): Float? = height
        override suspend fun setHeightValue(value: Float) { height = value }
        override suspend fun getWeightValue(): Float? = weight
        override suspend fun setWeightValue(value: Float) { weight = value }
        override suspend fun getAgeValue(): Float? = age
        override suspend fun setAgeValue(value: Float) { age = value }
        override suspend fun getGenderValue(): String = gender
        override suspend fun setGenderValue(value: String) { gender = value }
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
        override suspend fun getAuthMethod(): String = "none"
        override suspend fun setAuthMethod(value: String) = Unit
        override suspend fun getAuthEmail(): String? = null
        override suspend fun setAuthEmail(value: String?) = Unit
    }
}



