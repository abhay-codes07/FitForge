package com.fitforge.app.presentation.analytics

import com.fitforge.app.domain.usecase.analytics.AnalyticsOverviewData
import com.fitforge.app.domain.usecase.analytics.GetAnalyticsOverviewDataUseCase
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
class AnalyticsOverviewViewModelTest {
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
    fun `loads analytics on init`() = runTest(dispatcher) {
        val useCase = mockk<GetAnalyticsOverviewDataUseCase>()
        coEvery { useCase.invoke(any(), any(), any()) } returns AnalyticsOverviewData(
            isGuest = false,
            rangeDays = 30,
            totalSteps = 15000,
            totalCalories = 1100,
            totalWorkoutMinutes = 180,
            completedWorkouts = 5,
            averageWorkoutMinutes = 60,
            weightChangeKg = -1.2f,
            stepTrend = listOf(1000, 2000, 3000),
        )

        val viewModel = AnalyticsOverviewViewModel(useCase)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("15000", viewModel.uiState.value.totalSteps)
    }

    @Test
    fun `updates range selection`() = runTest(dispatcher) {
        val useCase = mockk<GetAnalyticsOverviewDataUseCase>()
        coEvery { useCase.invoke(any(), any(), any()) } returns AnalyticsOverviewData(
            isGuest = false,
            rangeDays = 7,
            totalSteps = 7000,
            totalCalories = 500,
            totalWorkoutMinutes = 70,
            completedWorkouts = 2,
            averageWorkoutMinutes = 35,
            weightChangeKg = null,
            stepTrend = listOf(1000, 1000),
        )

        val viewModel = AnalyticsOverviewViewModel(useCase)
        advanceUntilIdle()

        viewModel.onRangeSelected(7)
        advanceUntilIdle()

        assertEquals(7, viewModel.uiState.value.selectedRangeDays)
    }
}
