package com.fitforge.app.data.repository

import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.domain.repository.OnboardingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class OnboardingRepositoryImpl @Inject constructor(
    private val userPrefs: UserPrefs,
) : OnboardingRepository {
    override suspend fun isOnboardingComplete(): Boolean = userPrefs.isOnboardingComplete.first()
}

