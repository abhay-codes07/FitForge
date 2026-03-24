package com.fitforge.app.presentation.onboarding.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Spacing

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onEmailContinue: () -> Unit,
    onGoogleContinue: () -> Unit,
    onGuestContinue: () -> Unit,
    onNavigateNext: (String) -> Unit,
) {
    LaunchedEffect(uiState.destinationRoute) {
        uiState.destinationRoute?.let(onNavigateNext)
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
                    text = "You can continue with email, choose Google for a faster setup, or start as a guest and link an account later.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
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
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )

            Button(
                onClick = onEmailContinue,
                enabled = uiState.canContinueWithEmail,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_continue"),
                colors = ButtonDefaults.buttonColors(containerColor = Purple600),
            ) {
                Text("Continue with email")
            }

            OutlinedButton(
                onClick = onGoogleContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_google_continue"),
            ) {
                Text("Continue with Google")
            }

            OutlinedButton(
                onClick = onGuestContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_guest_continue"),
            ) {
                Text("Continue as guest")
            }
        }
    }
}
