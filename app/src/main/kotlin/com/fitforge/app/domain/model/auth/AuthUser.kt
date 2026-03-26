package com.fitforge.app.domain.model.auth

data class AuthUser(
    val uid: String,
    val email: String?,
    val isAnonymous: Boolean,
)
