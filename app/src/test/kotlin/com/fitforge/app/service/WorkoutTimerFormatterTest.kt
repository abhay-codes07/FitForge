package com.fitforge.app.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WorkoutTimerFormatterTest {
    @Test
    fun `formats zero seconds`() {
        assertEquals("00:00 remaining", WorkoutTimerFormatter.formatRemainingTime(0))
    }

    @Test
    fun `formats minutes and seconds`() {
        assertEquals("02:05 remaining", WorkoutTimerFormatter.formatRemainingTime(125))
    }

    @Test
    fun `clamps negative values`() {
        assertEquals("00:00 remaining", WorkoutTimerFormatter.formatRemainingTime(-9))
    }
}
