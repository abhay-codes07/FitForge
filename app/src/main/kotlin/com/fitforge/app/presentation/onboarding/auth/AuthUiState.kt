package com.fitforge.app.presentation.onboarding.auth

import com.fitforge.app.data.local.datastore.UserPrefs

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val authMethod: String = UserPrefs.AuthMethod.NONE,
    val destinationRoute: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
) {
    val canContinueWithEmail: Boolean
        get() = !isLoading && email.isNotBlank() && password.isNotBlank()
}
