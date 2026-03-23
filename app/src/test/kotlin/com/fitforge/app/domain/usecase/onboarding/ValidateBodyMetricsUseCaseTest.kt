package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.presentation.onboarding.bodymetrics.BodyMetricsUnitSystem
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ValidateBodyMetricsUseCaseTest {
    private val useCase = ValidateBodyMetricsUseCase()

    @Test
    fun `accepts valid metric inputs`() {
        val result = useCase(
            unitSystem = BodyMetricsUnitSystem.Metric,
            height = "178",
            weight = "78",
            age = "27",
        )

        assertTrue(result.isValid)
    }

    @Test
    fun `rejects invalid imperial height`() {
        val result = useCase(
            unitSystem = BodyMetricsUnitSystem.Imperial,
            height = "120",
            weight = "180",
            age = "27",
        )

        assertFalse(result.isValid)
        assertTrue(result.heightError != null)
    }
}

