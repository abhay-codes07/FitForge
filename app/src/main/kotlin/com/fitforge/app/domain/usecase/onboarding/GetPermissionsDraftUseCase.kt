package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.repository.OnboardingRepository
import javax.inject.Inject

data class PermissionsDraft(
    val notificationState: String,
    val healthConnectState: String,
)

class GetPermissionsDraftUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke(): PermissionsDraft = PermissionsDraft(
        notificationState = onboardingRepository.getNotificationPermissionState(),
        healthConnectState = onboardingRepository.getHealthConnectPermissionState(),
    )
}
