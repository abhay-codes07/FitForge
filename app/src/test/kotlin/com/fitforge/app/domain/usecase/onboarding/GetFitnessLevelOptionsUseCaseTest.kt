package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.data.local.datastore.UserPrefs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetFitnessLevelOptionsUseCaseTest {

    @Test
    fun `returns the three supported fitness levels in onboarding order`() {
        val options = GetFitnessLevelOptionsUseCase()()

        assertEquals(3, options.size)
        assertEquals(UserPrefs.FitnessLevel.BEGINNER, options[0].storageValue)
        assertEquals(UserPrefs.FitnessLevel.INTERMEDIATE, options[1].storageValue)
        assertEquals(UserPrefs.FitnessLevel.ADVANCED, options[2].storageValue)
    }
}
