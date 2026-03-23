package com.fitforge.app

import com.fitforge.app.util.UiState
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ArchitectureSanityTest {
    @Test
    fun `ui state success stores payload`() {
        val state = UiState.Success("FitForge")
        assertTrue(state.data == "FitForge")
    }
}
