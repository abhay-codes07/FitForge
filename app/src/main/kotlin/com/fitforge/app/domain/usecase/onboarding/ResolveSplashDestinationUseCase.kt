package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.navigation.Screen
import javax.inject.Inject

class ResolveSplashDestinationUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke(): String {
        return if (onboardingRepository.isOnboardingComplete()) {
            Screen.Home.route
        } else {
            Screen.Welcome.route
        }
    }
}

