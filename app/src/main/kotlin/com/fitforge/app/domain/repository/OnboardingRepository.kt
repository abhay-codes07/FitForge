package com.fitforge.app.domain.repository

interface OnboardingRepository {
    suspend fun isOnboardingComplete(): Boolean
    suspend fun setOnboardingComplete(completed: Boolean)
    suspend fun getSelectedGoals(): Set<String>
    suspend fun setSelectedGoals(values: Set<String>)
    suspend fun getPreferredUnitSystem(): String
    suspend fun setPreferredUnitSystem(value: String)
    suspend fun getHeightValue(): Float?
    suspend fun setHeightValue(value: Float)
    suspend fun getWeightValue(): Float?
    suspend fun setWeightValue(value: Float)
    suspend fun getAgeValue(): Float?
    suspend fun setAgeValue(value: Float)
    suspend fun getGenderValue(): String
    suspend fun setGenderValue(value: String)
    suspend fun getFitnessLevel(): String
    suspend fun setFitnessLevel(value: String)
    suspend fun getWorkoutLocations(): Set<String>
    suspend fun setWorkoutLocations(values: Set<String>)
    suspend fun getAvailableEquipment(): Set<String>
    suspend fun setAvailableEquipment(values: Set<String>)
    suspend fun getWorkoutDays(): Set<String>
    suspend fun setWorkoutDays(values: Set<String>)
    suspend fun getWorkoutDurationMinutes(): Int
    suspend fun setWorkoutDurationMinutes(value: Int)
    suspend fun getNotificationPermissionState(): String
    suspend fun setNotificationPermissionState(value: String)
    suspend fun getHealthConnectPermissionState(): String
    suspend fun setHealthConnectPermissionState(value: String)
    suspend fun getAuthMethod(): String
    suspend fun setAuthMethod(value: String)
    suspend fun getAuthEmail(): String?
    suspend fun setAuthEmail(value: String?)
}
