package com.fitforge.app.presentation.active_workout

import com.fitforge.app.domain.usecase.active_workout.GetStartWorkoutOptionsUseCase
import com.fitforge.app.domain.usecase.active_workout.StartWorkoutOptionsData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StartWorkoutViewModelTest {
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
    fun `loads options on init`() = runTest(dispatcher) {
        val useCase = mockk<GetStartWorkoutOptionsUseCase>()
        coEvery { useCase.invoke(any()) } returns StartWorkoutOptionsData(
            hasPrimaryUser = true,
            scheduledWorkoutCount = 2,
            options = listOf("Start Scheduled Workout", "Quick Workout"),
        )

        val viewModel = StartWorkoutViewModel(useCase)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(2, viewModel.uiState.value.options.size)
        assertEquals("2 scheduled this week", viewModel.uiState.value.scheduledWorkoutCountLabel)
    }

    @Test
    fun `select option updates state`() = runTest(dispatcher) {
        val useCase = mockk<GetStartWorkoutOptionsUseCase>()
        coEvery { useCase.invoke(any()) } returns StartWorkoutOptionsData(
            hasPrimaryUser = false,
            scheduledWorkoutCount = 0,
            options = listOf("Quick Workout"),
        )

        val viewModel = StartWorkoutViewModel(useCase)
        advanceUntilIdle()

        viewModel.onOptionSelected("Quick Workout")
        assertEquals("Quick Workout", viewModel.uiState.value.selectedOption)
    }
}
