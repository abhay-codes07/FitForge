package com.fitforge.app.presentation.home

import com.fitforge.app.domain.usecase.home.GetHomeDashboardDataUseCase
import com.fitforge.app.domain.usecase.home.HomeDashboardData
import com.fitforge.app.domain.usecase.sync.SyncAllDataUseCase
import com.fitforge.app.domain.usecase.sync.SyncResult
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeDashboardViewModelTest {
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
    fun `loads dashboard data on init`() = runTest(dispatcher) {
        val useCase = mockk<GetHomeDashboardDataUseCase>()
        val syncAllDataUseCase = mockk<SyncAllDataUseCase>()
        coEvery { syncAllDataUseCase.invoke() } returns Result.success(SyncResult(1, true, null))
        coEvery { useCase.invoke(any(), any()) } returns HomeDashboardData(
            isGuest = false,
            greetingName = "Abhay",
            steps = 8700,
            activeCalories = 480,
            workoutMinutes = 55,
            waterIntakeMl = 2100,
            latestWeightKg = 79.2f,
            nextWorkoutTitle = "Push Day",
            recentWorkoutTitles = listOf("Push Day", "Core Blast"),
        )

        val viewModel = HomeDashboardViewModel(useCase, syncAllDataUseCase)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Abhay", viewModel.uiState.value.greetingName)
        assertEquals("Push Day", viewModel.uiState.value.nextWorkoutTitle)
        assertEquals(2, viewModel.uiState.value.recentWorkoutTitles.size)
    }

    @Test
    fun `shows error when loading fails`() = runTest(dispatcher) {
        val useCase = mockk<GetHomeDashboardDataUseCase>()
        val syncAllDataUseCase = mockk<SyncAllDataUseCase>()
        coEvery { syncAllDataUseCase.invoke() } returns Result.success(SyncResult(0, false, null))
        coEvery { useCase.invoke(any(), any()) } throws IllegalStateException("boom")

        val viewModel = HomeDashboardViewModel(useCase, syncAllDataUseCase)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.errorMessage)
    }
}
