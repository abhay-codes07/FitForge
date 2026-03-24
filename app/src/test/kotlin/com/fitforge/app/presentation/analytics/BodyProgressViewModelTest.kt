package com.fitforge.app.presentation.analytics

import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import com.fitforge.app.domain.usecase.analytics.AddBodyMeasurementUseCase
import com.fitforge.app.domain.usecase.analytics.BodyProgressData
import com.fitforge.app.domain.usecase.analytics.GetBodyProgressDataUseCase
import io.mockk.coEvery
import io.mockk.every
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
class BodyProgressViewModelTest {
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
    fun `loads body progress on init`() = runTest(dispatcher) {
        val getUseCase = mockk<GetBodyProgressDataUseCase>()
        val addUseCase = mockk<AddBodyMeasurementUseCase>()

        coEvery { getUseCase.invoke(any(), any(), any()) } returns BodyProgressData(
            isGuest = false,
            latestWeightKg = 78.5f,
            latestBodyFatPercent = 18.1f,
            weightTrend = listOf(80f, 79f, 78.5f),
            bodyFatTrend = listOf(20f, 19f, 18.1f),
            photoUris = listOf("content://p1"),
            entries = listOf(
                mockk<BodyMeasurementEntity> {
                    every { id } returns "m1"
                    every { recordedAtEpochMillis } returns 100L
                    every { weightKg } returns 78.5f
                    every { bodyFatPercent } returns 18.1f
                },
            ),
        )

        val viewModel = BodyProgressViewModel(getUseCase, addUseCase)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("78.5 kg", viewModel.uiState.value.latestWeightLabel)
    }

    @Test
    fun `save measurement updates success state`() = runTest(dispatcher) {
        val getUseCase = mockk<GetBodyProgressDataUseCase>()
        val addUseCase = mockk<AddBodyMeasurementUseCase>()
        coEvery { getUseCase.invoke(any(), any(), any()) } returns BodyProgressData(
            isGuest = false,
            latestWeightKg = null,
            latestBodyFatPercent = null,
            weightTrend = emptyList(),
            bodyFatTrend = emptyList(),
            photoUris = emptyList(),
            entries = emptyList(),
        )
        coEvery { addUseCase.invoke(any(), any(), any(), any(), any()) } returns Unit

        val viewModel = BodyProgressViewModel(getUseCase, addUseCase)
        advanceUntilIdle()

        viewModel.onWeightChanged("77.9")
        viewModel.onSaveMeasurement()
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isSaving)
        assertEquals("Measurement saved", viewModel.uiState.value.successMessage)
    }
}
