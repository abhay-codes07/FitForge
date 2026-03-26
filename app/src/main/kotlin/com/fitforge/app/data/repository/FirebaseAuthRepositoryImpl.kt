package com.fitforge.app.data.repository

import com.fitforge.app.domain.model.auth.AuthResult
import com.fitforge.app.domain.model.auth.AuthUser
import com.fitforge.app.domain.repository.AuthRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    override suspend fun signInWithEmailPassword(email: String, password: String): AuthResult {
        return runCatching {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.toDomainUser() ?: error("Signed in user is unavailable.")
        }.fold(
            onSuccess = { AuthResult.Success(it) },
            onFailure = { AuthResult.Failure(it.userFacingMessage()) },
        )
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): AuthResult {
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            authResult.user?.toDomainUser() ?: error("Google account is unavailable.")
        }.fold(
            onSuccess = { AuthResult.Success(it) },
            onFailure = { AuthResult.Failure(it.userFacingMessage()) },
        )
    }

    override suspend fun signInAnonymously(): AuthResult {
        return runCatching {
            val authResult = firebaseAuth.signInAnonymously().await()
            authResult.user?.toDomainUser() ?: error("Guest account is unavailable.")
        }.fold(
            onSuccess = { AuthResult.Success(it) },
            onFailure = { AuthResult.Failure(it.userFacingMessage()) },
        )
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return runCatching {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Unit
        }.recoverCatching { throw IllegalStateException(it.userFacingMessage()) }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): AuthUser? = firebaseAuth.currentUser?.toDomainUser()

    private fun Throwable.userFacingMessage(): String {
        val raw = message.orEmpty().trim()
        return if (raw.isBlank()) {
            "Authentication failed. Please try again."
        } else {
            raw
        }
    }
}

private fun com.google.firebase.auth.FirebaseUser.toDomainUser(): AuthUser = AuthUser(
    uid = uid,
    email = email,
    isAnonymous = isAnonymous,
)

private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result)
        } else {
            continuation.resumeWithException(task.exception ?: IllegalStateException("Task failed."))
        }
    }
}
