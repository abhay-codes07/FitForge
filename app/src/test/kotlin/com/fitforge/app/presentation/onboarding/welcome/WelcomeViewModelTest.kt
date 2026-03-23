package com.fitforge.app.presentation.onboarding.welcome

import com.fitforge.app.domain.model.onboarding.WelcomePage
import com.fitforge.app.domain.repository.WelcomeRepository
import com.fitforge.app.domain.usecase.onboarding.GetWelcomePagesUseCase
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WelcomeViewModelTest {
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
    fun `loads welcome pages on init`() = runTest(dispatcher) {
        val pages = listOf(
            WelcomePage("Title", "Description", emptyList(), "Label", "9"),
        )
        val viewModel = WelcomeViewModel(
            getWelcomePagesUseCase = GetWelcomePagesUseCase(
                welcomeRepository = object : WelcomeRepository {
                    override suspend fun getWelcomePages(): List<WelcomePage> = pages
                },
            ),
        )

        advanceUntilIdle()

        assertEquals(pages, viewModel.uiState.value.pages)
    }

    @Test
    fun `emits goal selection destination on get started`() = runTest(dispatcher) {
        val viewModel = WelcomeViewModel(
            getWelcomePagesUseCase = GetWelcomePagesUseCase(
                welcomeRepository = object : WelcomeRepository {
                    override suspend fun getWelcomePages(): List<WelcomePage> = emptyList()
                },
            ),
        )

        advanceUntilIdle()
        viewModel.onGetStartedClick()

        assertEquals(Screen.GoalSelection.route, viewModel.uiState.value.destinationRoute)
    }
}

