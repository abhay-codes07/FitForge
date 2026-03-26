package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.model.auth.AuthResult
import com.fitforge.app.domain.model.auth.AuthUser
import com.fitforge.app.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseAuthUseCasesTest {

    @Test
    fun `email sign in trims email before repository call`() = runTest {
        val repository = FakeAuthRepository()

        val result = SignInWithEmailUseCase(repository)("  user@example.com  ", "password123")

        assertTrue(result is AuthResult.Success)
        assertEquals("user@example.com", repository.lastEmail)
    }

    @Test
    fun `google sign in forwards id token`() = runTest {
        val repository = FakeAuthRepository()

        SignInWithGoogleUseCase(repository)("google_token")

        assertEquals("google_token", repository.lastGoogleToken)
    }

    @Test
    fun `password reset trims email`() = runTest {
        val repository = FakeAuthRepository()

        SendPasswordResetUseCase(repository)("  user@example.com  ")

        assertEquals("user@example.com", repository.lastResetEmail)
    }

    private class FakeAuthRepository : AuthRepository {
        var lastEmail: String? = null
        var lastPassword: String? = null
        var lastGoogleToken: String? = null
        var lastResetEmail: String? = null

        override suspend fun signInWithEmailPassword(email: String, password: String): AuthResult {
            lastEmail = email
            lastPassword = password
            return AuthResult.Success(AuthUser(uid = "1", email = email, isAnonymous = false))
        }

        override suspend fun signInWithGoogleIdToken(idToken: String): AuthResult {
            lastGoogleToken = idToken
            return AuthResult.Success(AuthUser(uid = "2", email = "google@example.com", isAnonymous = false))
        }

        override suspend fun signInAnonymously(): AuthResult {
            return AuthResult.Success(AuthUser(uid = "3", email = null, isAnonymous = true))
        }

        override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
            lastResetEmail = email
            return Result.success(Unit)
        }

        override suspend fun signOut() = Unit

        override fun getCurrentUser(): AuthUser? = null
    }
}
