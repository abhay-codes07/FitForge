package com.fitforge.app.domain.repository

import com.fitforge.app.domain.model.onboarding.WelcomePage

interface WelcomeRepository {
    suspend fun getWelcomePages(): List<WelcomePage>
}

