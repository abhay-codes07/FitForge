package com.fitforge.app.data.repository

import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.domain.repository.OnboardingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class OnboardingRepositoryImpl @Inject constructor(
    private val userPrefs: UserPrefs,
) : OnboardingRepository {
    override suspend fun isOnboardingComplete(): Boolean = userPrefs.isOnboardingComplete.first()
    override suspend fun getSelectedGoals(): Set<String> = userPrefs.selectedGoals.first()
    override suspend fun setSelectedGoals(values: Set<String>) {
        userPrefs.setSelectedGoals(values)
    }
    override suspend fun getPreferredUnitSystem(): String = userPrefs.preferredUnitSystem.first()
    override suspend fun setPreferredUnitSystem(value: String) {
        userPrefs.setPreferredUnitSystem(value)
    }
    override suspend fun getHeightValue(): Float? = userPrefs.heightValue.first()
    override suspend fun setHeightValue(value: Float) {
        userPrefs.setHeightValue(value)
    }
    override suspend fun getWeightValue(): Float? = userPrefs.weightValue.first()
    override suspend fun setWeightValue(value: Float) {
        userPrefs.setWeightValue(value)
    }
    override suspend fun getAgeValue(): Float? = userPrefs.ageValue.first()
    override suspend fun setAgeValue(value: Float) {
        userPrefs.setAgeValue(value)
    }
    override suspend fun getGenderValue(): String = userPrefs.genderValue.first()
    override suspend fun setGenderValue(value: String) {
        userPrefs.setGenderValue(value)
    }
    override suspend fun getFitnessLevel(): String = userPrefs.fitnessLevel.first()
    override suspend fun setFitnessLevel(value: String) {
        userPrefs.setFitnessLevel(value)
    }
    override suspend fun getWorkoutLocations(): Set<String> = userPrefs.workoutLocations.first()
    override suspend fun setWorkoutLocations(values: Set<String>) {
        userPrefs.setWorkoutLocations(values)
    }
    override suspend fun getAvailableEquipment(): Set<String> = userPrefs.availableEquipment.first()
    override suspend fun setAvailableEquipment(values: Set<String>) {
        userPrefs.setAvailableEquipment(values)
    }
}
