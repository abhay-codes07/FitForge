package com.fitforge.app.domain.usecase.onboarding

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ValidateWorkoutPreferencesUseCaseTest {
    private val useCase = ValidateWorkoutPreferencesUseCase()

    @Test
    fun `fails when no location is selected`() {
        val result = useCase(emptySet())

        assertFalse(result.isValid)
        assertTrue(result.locationError != null)
    }

    @Test
    fun `passes when at least one location is selected`() {
        val result = useCase(setOf("home"))

        assertTrue(result.isValid)
    }
}
