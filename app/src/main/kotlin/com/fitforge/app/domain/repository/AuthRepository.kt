package com.fitforge.app.domain.repository

import com.fitforge.app.domain.model.auth.AuthResult
import com.fitforge.app.domain.model.auth.AuthUser

interface AuthRepository {
    suspend fun signInWithEmailPassword(email: String, password: String): AuthResult
    suspend fun signInWithGoogleIdToken(idToken: String): AuthResult
    suspend fun signInAnonymously(): AuthResult
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun signOut()
    fun getCurrentUser(): AuthUser?
}
