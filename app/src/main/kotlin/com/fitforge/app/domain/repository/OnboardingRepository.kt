package com.fitforge.app.domain.repository

interface OnboardingRepository {
    suspend fun isOnboardingComplete(): Boolean
    suspend fun getSelectedGoals(): Set<String>
    suspend fun setSelectedGoals(values: Set<String>)
}
