package com.fitforge.app.presentation.profile

import com.fitforge.app.domain.usecase.profile.ObserveProfileSettingsUseCase
import com.fitforge.app.domain.usecase.profile.ProfileSettingsData
import com.fitforge.app.domain.usecase.profile.UpdateThemePreferenceUseCase
import com.fitforge.app.domain.usecase.profile.UpdateUnitSystemPreferenceUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class ProfileViewModelTest {
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
    fun `loads profile settings`() = runTest(dispatcher) {
        val observe = mockk<ObserveProfileSettingsUseCase>()
        val updateTheme = mockk<UpdateThemePreferenceUseCase>()
        val updateUnit = mockk<UpdateUnitSystemPreferenceUseCase>()

        every { observe.invoke() } returns flowOf(
            ProfileSettingsData(
                displayName = "Abhay",
                email = "abhay@fitforge.dev",
                fitnessLevel = "intermediate",
                goals = setOf("muscle_gain"),
                themePreference = "dark",
                preferredUnitSystem = "metric",
                notificationsEnabled = true,
            ),
        )
        coEvery { updateTheme.invoke(any()) } returns Unit
        coEvery { updateUnit.invoke(any()) } returns Unit

        val viewModel = ProfileViewModel(observe, updateTheme, updateUnit)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Abhay", viewModel.uiState.value.displayName)
    }

    @Test
    fun `theme and unit actions delegate`() = runTest(dispatcher) {
        val observe = mockk<ObserveProfileSettingsUseCase>()
        val updateTheme = mockk<UpdateThemePreferenceUseCase>()
        val updateUnit = mockk<UpdateUnitSystemPreferenceUseCase>()
        every { observe.invoke() } returns flowOf(
            ProfileSettingsData(
                displayName = "Abhay",
                email = null,
                fitnessLevel = "beginner",
                goals = emptySet(),
                themePreference = "system",
                preferredUnitSystem = "metric",
                notificationsEnabled = false,
            ),
        )
        coEvery { updateTheme.invoke(any()) } returns Unit
        coEvery { updateUnit.invoke(any()) } returns Unit

        val viewModel = ProfileViewModel(observe, updateTheme, updateUnit)
        advanceUntilIdle()

        viewModel.onThemeSelected("dark")
        viewModel.onUnitSystemSelected("imperial")
        advanceUntilIdle()

        assertEquals(null, viewModel.uiState.value.errorMessage)
    }
}
