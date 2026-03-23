package com.fitforge.app.domain.usecase.onboarding

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetScheduleOptionsUseCaseTest {
    @Test
    fun `returns all days in weekly order`() {
        val options = GetScheduleOptionsUseCase()()

        assertEquals(listOf("mon", "tue", "wed", "thu", "fri", "sat", "sun"), options.map { it.storageValue })
        assertEquals(7, options.size)
    }
}
