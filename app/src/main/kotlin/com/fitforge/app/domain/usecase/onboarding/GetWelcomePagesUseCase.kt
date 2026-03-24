package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.model.onboarding.WelcomePage
import com.fitforge.app.domain.repository.WelcomeRepository
import javax.inject.Inject

class GetWelcomePagesUseCase @Inject constructor(
    private val welcomeRepository: WelcomeRepository,
) {
    suspend operator fun invoke(): List<WelcomePage> = welcomeRepository.getWelcomePages()
}
