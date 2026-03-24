package com.fitforge.app.domain.usecase.profile

import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProfileSettingsUseCasesTest {
    @Test
    fun `observe profile settings combines user and prefs`() = runTest {
        val userRepo = mockk<UserRepository>()
        val prefs = mockk<UserPrefs>()

        val user = mockk<UserEntity> {
            every { displayName } returns "Abhay"
            every { email } returns "abhay@fitforge.dev"
            every { fitnessLevel } returns "intermediate"
            every { primaryGoals } returns setOf("muscle_gain")
            every { isNotificationEnabled } returns true
        }
        every { userRepo.observePrimaryUser() } returns flowOf(user)
        every { prefs.themePreference } returns flowOf(UserPrefs.ThemePreference.DARK)
        every { prefs.preferredUnitSystem } returns flowOf(UserPrefs.UnitSystem.IMPERIAL)
        every { prefs.selectedGoals } returns flowOf(setOf("fat_loss"))

        val result = ObserveProfileSettingsUseCase(userRepo, prefs)().first()

        assertEquals("Abhay", result.displayName)
        assertEquals(UserPrefs.ThemePreference.DARK, result.themePreference)
        assertEquals(UserPrefs.UnitSystem.IMPERIAL, result.preferredUnitSystem)
        assertEquals(setOf("fat_loss"), result.goals)
    }

    @Test
    fun `update theme validates values`() = runTest {
        val prefs = mockk<UserPrefs>()
        coEvery { prefs.setThemePreference(any()) } returns Unit

        UpdateThemePreferenceUseCase(prefs)(UserPrefs.ThemePreference.LIGHT)

        var thrown: Throwable? = null
        try {
            UpdateThemePreferenceUseCase(prefs)("broken")
        } catch (t: Throwable) {
            thrown = t
        }
        assertEquals(IllegalArgumentException::class, thrown!!::class)
    }
}
