package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.repository.OnboardingRepository
import javax.inject.Inject

data class AuthDraft(
    val authMethod: String,
    val email: String,
)

class GetAuthDraftUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke(): AuthDraft = AuthDraft(
        authMethod = onboardingRepository.getAuthMethod(),
        email = onboardingRepository.getAuthEmail().orEmpty(),
    )
}

data class AuthValidationResult(
    val isValid: Boolean,
    val emailError: String? = null,
    val passwordError: String? = null,
)

class ValidateAuthCredentialsUseCase @Inject constructor() {
    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }

    operator fun invoke(email: String, password: String): AuthValidationResult {
        val normalizedEmail = email.trim()
        val emailError = validateEmail(normalizedEmail)
        val passwordError = when {
            password.isBlank() -> "Enter a password."
            password.length < 8 -> "Password must be at least 8 characters."
            else -> null
        }
        return AuthValidationResult(
            isValid = emailError == null && passwordError == null,
            emailError = emailError,
            passwordError = passwordError,
        )
    }

    fun validateEmail(email: String): String? {
        val normalizedEmail = email.trim()
        return when {
            normalizedEmail.isBlank() -> "Enter an email address."
            !EMAIL_REGEX.matches(normalizedEmail) -> "Enter a valid email address."
            else -> null
        }
    }
}

class CompleteOnboardingWithAuthUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke(
        authMethod: String,
        email: String?,
    ) {
        onboardingRepository.setAuthMethod(authMethod)
        onboardingRepository.setAuthEmail(email)
        onboardingRepository.setOnboardingComplete(true)
    }
}
