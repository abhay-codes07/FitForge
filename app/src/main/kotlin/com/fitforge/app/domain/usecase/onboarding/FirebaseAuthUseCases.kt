package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.model.auth.AuthResult
import com.fitforge.app.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        return authRepository.signInWithEmailPassword(email = email.trim(), password = password)
    }
}

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(idToken: String): AuthResult {
        return authRepository.signInWithGoogleIdToken(idToken = idToken)
    }
}

class SignInAnonymouslyUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): AuthResult = authRepository.signInAnonymously()
}

class SendPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return authRepository.sendPasswordResetEmail(email = email.trim())
    }
}

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() = authRepository.signOut()
}
