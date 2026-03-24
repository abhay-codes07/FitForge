package com.fitforge.app.domain.usecase.onboarding

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ValidateScheduleSetupUseCaseTest {
    private val useCase = ValidateScheduleSetupUseCase()

    @Test
    fun `fails when no days are selected`() {
        val result = useCase(emptySet())
        assertFalse(result.isValid)
        assertTrue(result.dayError != null)
    }

    @Test
    fun `passes when at least one day is selected`() {
        val result = useCase(setOf("mon"))
        assertTrue(result.isValid)
    }
}
