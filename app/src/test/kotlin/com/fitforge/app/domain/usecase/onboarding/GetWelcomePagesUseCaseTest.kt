package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.model.onboarding.WelcomePage
import com.fitforge.app.domain.repository.WelcomeRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetWelcomePagesUseCaseTest {

    @Test
    fun `returns repository welcome pages`() = runTest {
        val expected = listOf(
            WelcomePage("A", "B", emptyList(), "C", "D"),
            WelcomePage("E", "F", emptyList(), "G", "H"),
        )
        val useCase = GetWelcomePagesUseCase(
            welcomeRepository = object : WelcomeRepository {
                override suspend fun getWelcomePages(): List<WelcomePage> = expected
            },
        )

        assertEquals(expected, useCase())
    }
}

