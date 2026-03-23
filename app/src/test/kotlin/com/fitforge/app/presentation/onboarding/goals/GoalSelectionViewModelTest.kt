package com.fitforge.app.presentation.onboarding.goals

import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.domain.usecase.onboarding.GetGoalOptionsUseCase
import com.fitforge.app.domain.usecase.onboarding.GetSelectedGoalsUseCase
import com.fitforge.app.domain.usecase.onboarding.SaveGoalSelectionUseCase
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
class GoalSelectionViewModelTest {
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
    fun `loads goals and restores saved selection`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository(setOf("muscle_gain"))
        val viewModel = GoalSelectionViewModel(
            getGoalOptionsUseCase = GetGoalOptionsUseCase(),
            getSelectedGoalsUseCase = GetSelectedGoalsUseCase(repository),
            saveGoalSelectionUseCase = SaveGoalSelectionUseCase(repository),
        )

        advanceUntilIdle()

        assertEquals(setOf("muscle_gain"), viewModel.uiState.value.selectedGoals)
        assertTrue(viewModel.uiState.value.canContinue)
        assertEquals(5, viewModel.uiState.value.options.size)
    }

    @Test
    fun `toggles goals on and off`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository(emptySet())
        val viewModel = GoalSelectionViewModel(
            getGoalOptionsUseCase = GetGoalOptionsUseCase(),
            getSelectedGoalsUseCase = GetSelectedGoalsUseCase(repository),
            saveGoalSelectionUseCase = SaveGoalSelectionUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.onGoalToggle("weight_loss")
        assertTrue("weight_loss" in viewModel.uiState.value.selectedGoals)
        assertTrue(viewModel.uiState.value.canContinue)

        viewModel.onGoalToggle("weight_loss")
        assertFalse("weight_loss" in viewModel.uiState.value.selectedGoals)
        assertFalse(viewModel.uiState.value.canContinue)
    }

    @Test
    fun `saves goals and routes to body metrics on continue`() = runTest(dispatcher) {
        val repository = FakeOnboardingRepository(emptySet())
        val viewModel = GoalSelectionViewModel(
            getGoalOptionsUseCase = GetGoalOptionsUseCase(),
            getSelectedGoalsUseCase = GetSelectedGoalsUseCase(repository),
            saveGoalSelectionUseCase = SaveGoalSelectionUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.onGoalToggle("stay_fit")
        viewModel.onContinueClick()
        advanceUntilIdle()

        assertEquals(setOf("stay_fit"), repository.savedGoals)
        assertEquals(Screen.BodyMetrics.route, viewModel.uiState.value.destinationRoute)
    }

    private class FakeOnboardingRepository(
        initialGoals: Set<String>,
    ) : OnboardingRepository {
        var savedGoals: Set<String> = initialGoals

        override suspend fun isOnboardingComplete(): Boolean = false

        override suspend fun getSelectedGoals(): Set<String> = savedGoals

        override suspend fun setSelectedGoals(values: Set<String>) {
            savedGoals = values
        }
    }
}

