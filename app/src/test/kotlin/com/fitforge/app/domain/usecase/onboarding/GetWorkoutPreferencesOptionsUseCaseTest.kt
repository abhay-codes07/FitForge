package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.data.local.datastore.UserPrefs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetWorkoutPreferencesOptionsUseCaseTest {
    @Test
    fun `returns all supported workout locations and equipment options`() {
        val (locations, equipment) = GetWorkoutPreferencesOptionsUseCase()()

        assertEquals(listOf(
            UserPrefs.WorkoutLocation.HOME,
            UserPrefs.WorkoutLocation.GYM,
            UserPrefs.WorkoutLocation.OUTDOOR,
        ), locations.map { it.storageValue })
        assertEquals(UserPrefs.Equipment.NONE, equipment.first().storageValue)
        assertEquals(8, equipment.size)
    }
}
