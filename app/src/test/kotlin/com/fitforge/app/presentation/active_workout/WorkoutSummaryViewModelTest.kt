package com.fitforge.app.presentation.active_workout

import com.fitforge.app.domain.usecase.active_workout.GetWorkoutSummaryDataUseCase
import com.fitforge.app.domain.usecase.active_workout.WorkoutSummaryData
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
class WorkoutSummaryViewModelTest {
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
    fun `loads workout summary on init`() = runTest(dispatcher) {
        val useCase = mockk<GetWorkoutSummaryDataUseCase>()
        coEvery { useCase.invoke(any()) } returns WorkoutSummaryData(
            workoutId = "w1",
            workoutName = "Leg Day",
            completedAtEpochMillis = 1000L,
            estimatedDurationMinutes = 40,
            estimatedCalories = 320,
            totalSets = 12,
            totalReps = 96,
            totalVolumeKg = 2400f,
            personalRecordCount = 2,
        )

        val viewModel = WorkoutSummaryViewModel(useCase)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Leg Day", viewModel.uiState.value.workoutName)
        assertEquals("12", viewModel.uiState.value.totalSetsLabel)
    }

    @Test
    fun `shows error state when use case fails`() = runTest(dispatcher) {
        val useCase = mockk<GetWorkoutSummaryDataUseCase>()
        coEvery { useCase.invoke(any()) } throws IllegalStateException("No completed workout available")

        val viewModel = WorkoutSummaryViewModel(useCase)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("No completed workout available", viewModel.uiState.value.errorMessage)
    }
}
