package com.fitforge.app.domain.repository

interface OnboardingRepository {
    suspend fun isOnboardingComplete(): Boolean
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
}
