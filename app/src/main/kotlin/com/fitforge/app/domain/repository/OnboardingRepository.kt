package com.fitforge.app.domain.repository

interface OnboardingRepository {
    suspend fun isOnboardingComplete(): Boolean
}

