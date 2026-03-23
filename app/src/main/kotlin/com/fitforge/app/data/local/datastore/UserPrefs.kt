package com.fitforge.app.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Singleton
class UserPrefs @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val dataStore = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile(DATASTORE_NAME)
    }

    val isOnboardingComplete: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.OnboardingComplete] ?: false }

    val themePreference: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.ThemePreference] ?: ThemePreference.SYSTEM }

    val selectedGoals: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.SelectedGoals] ?: emptySet() }

    val preferredUnitSystem: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.PreferredUnitSystem] ?: UnitSystem.METRIC }

    val heightValue: Flow<Float?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.HeightValue] }

    val weightValue: Flow<Float?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.WeightValue] }

    val ageValue: Flow<Float?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.AgeValue] }

    val genderValue: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.GenderValue] ?: Gender.UNSPECIFIED }

    val fitnessLevel: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.FitnessLevel] ?: FitnessLevel.UNSPECIFIED }

    val workoutLocations: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.WorkoutLocations] ?: emptySet() }

    val availableEquipment: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.AvailableEquipment] ?: emptySet() }

    val workoutDays: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.WorkoutDays] ?: emptySet() }

    val workoutDurationMinutes: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.WorkoutDurationMinutes] ?: 30 }

    suspend fun setOnboardingComplete(completed: Boolean) {
        dataStore.edit { prefs -> prefs[Keys.OnboardingComplete] = completed }
    }

    suspend fun setThemePreference(value: String) {
        dataStore.edit { prefs -> prefs[Keys.ThemePreference] = value }
    }

    suspend fun setSelectedGoals(values: Set<String>) {
        dataStore.edit { prefs -> prefs[Keys.SelectedGoals] = values }
    }

    suspend fun setPreferredUnitSystem(value: String) {
        dataStore.edit { prefs -> prefs[Keys.PreferredUnitSystem] = value }
    }

    suspend fun setHeightValue(value: Float) {
        dataStore.edit { prefs -> prefs[Keys.HeightValue] = value }
    }

    suspend fun setWeightValue(value: Float) {
        dataStore.edit { prefs -> prefs[Keys.WeightValue] = value }
    }

    suspend fun setAgeValue(value: Float) {
        dataStore.edit { prefs -> prefs[Keys.AgeValue] = value }
    }

    suspend fun setGenderValue(value: String) {
        dataStore.edit { prefs -> prefs[Keys.GenderValue] = value }
    }

    suspend fun setFitnessLevel(value: String) {
        dataStore.edit { prefs -> prefs[Keys.FitnessLevel] = value }
    }

    suspend fun setWorkoutLocations(values: Set<String>) {
        dataStore.edit { prefs -> prefs[Keys.WorkoutLocations] = values }
    }

    suspend fun setAvailableEquipment(values: Set<String>) {
        dataStore.edit { prefs -> prefs[Keys.AvailableEquipment] = values }
    }

    suspend fun setWorkoutDays(values: Set<String>) {
        dataStore.edit { prefs -> prefs[Keys.WorkoutDays] = values }
    }

    suspend fun setWorkoutDurationMinutes(value: Int) {
        dataStore.edit { prefs -> prefs[Keys.WorkoutDurationMinutes] = value }
    }

    private object Keys {
        val OnboardingComplete = booleanPreferencesKey("onboarding_complete")
        val ThemePreference = stringPreferencesKey("theme_preference")
        val SelectedGoals = stringSetPreferencesKey("selected_goals")
        val PreferredUnitSystem = stringPreferencesKey("preferred_unit_system")
        val HeightValue = floatPreferencesKey("height_value")
        val WeightValue = floatPreferencesKey("weight_value")
        val AgeValue = floatPreferencesKey("age_value")
        val GenderValue = stringPreferencesKey("gender_value")
        val FitnessLevel = stringPreferencesKey("fitness_level")
        val WorkoutLocations = stringSetPreferencesKey("workout_locations")
        val AvailableEquipment = stringSetPreferencesKey("available_equipment")
        val WorkoutDays = stringSetPreferencesKey("workout_days")
        val WorkoutDurationMinutes = intPreferencesKey("workout_duration_minutes")
    }

    object ThemePreference {
        const val LIGHT = "light"
        const val DARK = "dark"
        const val SYSTEM = "system"
    }

    object UnitSystem {
        const val METRIC = "metric"
        const val IMPERIAL = "imperial"
    }

    object Gender {
        const val FEMALE = "female"
        const val MALE = "male"
        const val NON_BINARY = "non_binary"
        const val UNSPECIFIED = "unspecified"
    }

    object FitnessLevel {
        const val BEGINNER = "beginner"
        const val INTERMEDIATE = "intermediate"
        const val ADVANCED = "advanced"
        const val UNSPECIFIED = "unspecified"
    }

    object WorkoutLocation {
        const val HOME = "home"
        const val GYM = "gym"
        const val OUTDOOR = "outdoor"
    }

    object Equipment {
        const val NONE = "none"
        const val DUMBBELLS = "dumbbells"
        const val RESISTANCE_BANDS = "resistance_bands"
        const val KETTLEBELL = "kettlebell"
        const val BARBELL = "barbell"
        const val BENCH = "bench"
        const val TREADMILL = "treadmill"
        const val YOGA_MAT = "yoga_mat"
    }

    object WorkoutDay {
        const val MONDAY = "mon"
        const val TUESDAY = "tue"
        const val WEDNESDAY = "wed"
        const val THURSDAY = "thu"
        const val FRIDAY = "fri"
        const val SATURDAY = "sat"
        const val SUNDAY = "sun"
    }

    private companion object {
        const val DATASTORE_NAME = "fitforge_user_prefs"
    }
}
