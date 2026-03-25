package com.fitforge.app.presentation.gps_tracking

import com.fitforge.app.domain.usecase.gps_tracking.CompleteGpsWorkoutSessionUseCase
import com.fitforge.app.domain.usecase.gps_tracking.CreateGpsWorkoutSessionUseCase
import com.fitforge.app.domain.usecase.gps_tracking.ObserveGpsRoutePointsUseCase
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
class GpsTrackingViewModelTest {
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
    fun `mode selection updates ui state`() = runTest(dispatcher) {
        val createUseCase = mockk<CreateGpsWorkoutSessionUseCase>()
        val observeUseCase = mockk<ObserveGpsRoutePointsUseCase>()
        val completeUseCase = mockk<CompleteGpsWorkoutSessionUseCase>()

        val viewModel = GpsTrackingViewModel(createUseCase, observeUseCase, completeUseCase)
        viewModel.onModeSelected("Cycle")

        assertEquals("Cycle", viewModel.uiState.value.selectedMode)
    }

    @Test
    fun `start tracking failure surfaces error message`() = runTest(dispatcher) {
        val createUseCase = mockk<CreateGpsWorkoutSessionUseCase>()
        val observeUseCase = mockk<ObserveGpsRoutePointsUseCase>()
        val completeUseCase = mockk<CompleteGpsWorkoutSessionUseCase>()

        coEvery { createUseCase.invoke(any(), any()) } throws IllegalStateException("GPS unavailable")

        val viewModel = GpsTrackingViewModel(createUseCase, observeUseCase, completeUseCase)
        viewModel.onStartTrackingClick()
        advanceUntilIdle()

        assertEquals("GPS unavailable", viewModel.uiState.value.errorMessage)
        assertEquals(false, viewModel.uiState.value.isTracking)
    }
}
