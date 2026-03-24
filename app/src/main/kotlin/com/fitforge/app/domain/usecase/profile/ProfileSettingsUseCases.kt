package com.fitforge.app.domain.usecase.profile

import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class ProfileSettingsData(
    val displayName: String,
    val email: String?,
    val fitnessLevel: String,
    val goals: Set<String>,
    val themePreference: String,
    val preferredUnitSystem: String,
    val notificationsEnabled: Boolean,
)

class ObserveProfileSettingsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userPrefs: UserPrefs,
) {
    operator fun invoke(): Flow<ProfileSettingsData> {
        return combine(
            userRepository.observePrimaryUser(),
            userPrefs.themePreference,
            userPrefs.preferredUnitSystem,
            userPrefs.selectedGoals,
        ) { user, theme, unitSystem, goals ->
            ProfileSettingsData(
                displayName = user?.displayName ?: "Athlete",
                email = user?.email,
                fitnessLevel = user?.fitnessLevel ?: UserPrefs.FitnessLevel.UNSPECIFIED,
                goals = if (goals.isNotEmpty()) goals else (user?.primaryGoals ?: emptySet()),
                themePreference = theme,
                preferredUnitSystem = unitSystem,
                notificationsEnabled = user?.isNotificationEnabled ?: false,
            )
        }
    }
}

class UpdateThemePreferenceUseCase @Inject constructor(
    private val userPrefs: UserPrefs,
) {
    suspend operator fun invoke(theme: String) {
        require(theme in setOf(UserPrefs.ThemePreference.LIGHT, UserPrefs.ThemePreference.DARK, UserPrefs.ThemePreference.SYSTEM)) {
            "Invalid theme preference"
        }
        userPrefs.setThemePreference(theme)
    }
}

class UpdateUnitSystemPreferenceUseCase @Inject constructor(
    private val userPrefs: UserPrefs,
) {
    suspend operator fun invoke(unitSystem: String) {
        require(unitSystem in setOf(UserPrefs.UnitSystem.METRIC, UserPrefs.UnitSystem.IMPERIAL)) {
            "Invalid unit system"
        }
        userPrefs.setPreferredUnitSystem(unitSystem)
    }
}
