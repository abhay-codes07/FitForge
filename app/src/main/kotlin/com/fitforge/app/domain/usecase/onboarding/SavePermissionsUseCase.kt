package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.repository.OnboardingRepository
import javax.inject.Inject

class SavePermissionsUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke(
        notificationState: String,
        healthConnectState: String,
    ) {
        onboardingRepository.setNotificationPermissionState(notificationState)
        onboardingRepository.setHealthConnectPermissionState(healthConnectState)
    }
}
