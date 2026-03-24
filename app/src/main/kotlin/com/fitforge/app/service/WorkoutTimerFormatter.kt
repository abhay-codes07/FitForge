package com.fitforge.app.service

import kotlin.math.max

object WorkoutTimerFormatter {
    fun formatRemainingTime(seconds: Int): String {
        val safeSeconds = max(seconds, 0)
        val minutes = safeSeconds / 60
        val remainder = safeSeconds % 60
        return String.format("%02d:%02d remaining", minutes, remainder)
    }
}
