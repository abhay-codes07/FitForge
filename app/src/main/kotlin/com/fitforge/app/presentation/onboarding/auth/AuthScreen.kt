package com.fitforge.app.presentation.onboarding.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.fitforge.app.R
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Spacing
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onEmailContinue: () -> Unit,
    onGoogleIdTokenReceived: (String) -> Unit,
    onGoogleSignInFailed: (String) -> Unit,
    onPasswordReset: () -> Unit,
    onGuestContinue: () -> Unit,
    onNavigateNext: (String) -> Unit,
    onNavigationHandled: () -> Unit,
) {
    val context = LocalContext.current
    val googleClientId = stringResource(R.string.google_web_client_id)

    val googleSignInClient = remember(googleClientId) {
        if (googleClientId.isBlank()) {
            null
        } else {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(googleClientId)
                .build()
            GoogleSignIn.getClient(context, options)
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK || result.data == null) {
            onGoogleSignInFailed("Google Sign-In was cancelled.")
            return@rememberLauncherForActivityResult
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken.isNullOrBlank()) {
                onGoogleSignInFailed("Google Sign-In did not return an ID token.")
            } else {
                onGoogleIdTokenReceived(idToken)
            }
        } catch (exception: ApiException) {
            onGoogleSignInFailed("Google Sign-In failed with code ${exception.statusCode}.")
        }
    }

    LaunchedEffect(uiState.destinationRoute) {
        uiState.destinationRoute?.let {
            onNavigateNext(it)
            onNavigationHandled()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("auth_screen"),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Text(
                    text = "Create your FitForge account",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Continue with email, Google, or guest mode. You can always link accounts later.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                )
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("auth_error_message"),
                )
            }

            if (uiState.infoMessage != null) {
                Text(
                    text = uiState.infoMessage,
                    color = Color(0xFF10B981),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("auth_info_message"),
                )
            }

            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email"),
                label = { Text("Email") },
                isError = uiState.emailError != null,
                supportingText = { uiState.emailError?.let { Text(it) } },
                singleLine = true,
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password"),
                label = { Text("Password") },
                isError = uiState.passwordError != null,
                supportingText = { uiState.passwordError?.let { Text(it) } },
                singleLine = true,
                enabled = !uiState.isLoading,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )

            TextButton(
                onClick = onPasswordReset,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_reset"),
            ) {
                Text("Forgot password?")
            }

            Button(
                onClick = onEmailContinue,
                enabled = uiState.canContinueWithEmail,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_continue"),
                colors = ButtonDefaults.buttonColors(containerColor = Purple600),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.testTag("auth_loading"),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("Continue with email")
                }
            }

            OutlinedButton(
                onClick = {
                    val client = googleSignInClient
                    if (client == null) {
                        onGoogleSignInFailed("Google Sign-In is not configured. Set google_web_client_id.")
                    } else {
                        launcher.launch(client.signInIntent)
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_google_continue"),
            ) {
                Text("Continue with Google")
            }

            OutlinedButton(
                onClick = onGuestContinue,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_guest_continue"),
            ) {
                Text("Continue as guest")
            }
        }
    }
}
