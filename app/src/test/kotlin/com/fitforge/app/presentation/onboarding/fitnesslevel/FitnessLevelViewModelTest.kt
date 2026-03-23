package com.fitforge.app.presentation.onboarding.fitnesslevel

import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.domain.usecase.onboarding.GetFitnessLevelOptionsUseCase
import com.fitforge.app.domain.usecase.onboarding.GetSelectedFitnessLevelUseCase
import com.fitforge.app.domain.usecase.onboarding.SaveFitnessLevelUseCase
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
class FitnessLevelViewModelTest {
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
    fun `loads options and restores saved selection`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository(UserPrefs.FitnessLevel.INTERMEDIATE)
        val viewModel = FitnessLevelViewModel(
            getFitnessLevelOptionsUseCase = GetFitnessLevelOptionsUseCase(),
            getSelectedFitnessLevelUseCase = GetSelectedFitnessLevelUseCase(repository),
            saveFitnessLevelUseCase = SaveFitnessLevelUseCase(repository),
        )

        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.options.size)
        assertEquals(UserPrefs.FitnessLevel.INTERMEDIATE, viewModel.uiState.value.selectedLevel)
        assertTrue(viewModel.uiState.value.canContinue)
    }

    @Test
    fun `updates selection and saves route on continue`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository(UserPrefs.FitnessLevel.UNSPECIFIED)
        val viewModel = FitnessLevelViewModel(
            getFitnessLevelOptionsUseCase = GetFitnessLevelOptionsUseCase(),
            getSelectedFitnessLevelUseCase = GetSelectedFitnessLevelUseCase(repository),
            saveFitnessLevelUseCase = SaveFitnessLevelUseCase(repository),
        )

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.canContinue)

        viewModel.onFitnessLevelSelected(UserPrefs.FitnessLevel.ADVANCED)
        viewModel.onContinueClick()
        advanceUntilIdle()

        assertEquals(UserPrefs.FitnessLevel.ADVANCED, repository.fitnessLevel)
        assertEquals(Screen.WorkoutPreferences.route, viewModel.uiState.value.destinationRoute)
    }

    private class FakeOnboardingRepository(
        var fitnessLevel: String,
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
        override suspend fun getFitnessLevel(): String = fitnessLevel
        override suspend fun setFitnessLevel(value: String) { fitnessLevel = value }
        override suspend fun getWorkoutLocations(): Set<String> = emptySet()
        override suspend fun setWorkoutLocations(values: Set<String>) = Unit
        override suspend fun getAvailableEquipment(): Set<String> = emptySet()
        override suspend fun setAvailableEquipment(values: Set<String>) = Unit
        override suspend fun getWorkoutDays(): Set<String> = emptySet()
        override suspend fun setWorkoutDays(values: Set<String>) = Unit
        override suspend fun getWorkoutDurationMinutes(): Int = 30
        override suspend fun setWorkoutDurationMinutes(value: Int) = Unit
    }
}


