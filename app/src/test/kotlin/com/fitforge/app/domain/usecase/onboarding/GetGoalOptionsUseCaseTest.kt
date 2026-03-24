package com.fitforge.app.domain.usecase.onboarding

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetGoalOptionsUseCaseTest {
    @Test
    fun `returns all configured onboarding goals`() {
        val options = GetGoalOptionsUseCase()()

        assertEquals(5, options.size)
        assertTrue(options.any { it.id == "weight_loss" })
        assertTrue(options.any { it.id == "endurance" })
    }
}

