package com.fitforge.app.domain.model.auth

sealed interface AuthResult {
    data class Success(val user: AuthUser) : AuthResult
    data class Failure(val message: String) : AuthResult
}

