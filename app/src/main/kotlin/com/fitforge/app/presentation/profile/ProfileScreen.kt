package com.fitforge.app.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.fitforge.app.data.local.datastore.UserPrefs
import com.fitforge.app.presentation.theme.LightSurface1
import com.fitforge.app.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onThemeSelected: (String) -> Unit,
    onUnitSystemSelected: (String) -> Unit,
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Profile & Settings") }) }) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("profile_loading"))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding)
                .testTag("profile_screen"),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = LightSurface1)) {
                Column(modifier = Modifier.padding(Spacing.cardInnerPadding), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(uiState.displayName, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.testTag("profile_name"))
                    Text(uiState.email, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.testTag("profile_email"))
                    Text("Level: ${uiState.fitnessLevel}", style = MaterialTheme.typography.bodySmall)
                    Text("Goals: ${uiState.goals.joinToString()}", style = MaterialTheme.typography.bodySmall)
                }
            }

            SettingsRow(
                title = "Theme",
                selected = uiState.themePreference,
                options = listOf(
                    UserPrefs.ThemePreference.SYSTEM,
                    UserPrefs.ThemePreference.LIGHT,
                    UserPrefs.ThemePreference.DARK,
                ),
                tagPrefix = "profile_theme_",
                onOptionClick = onThemeSelected,
            )

            SettingsRow(
                title = "Units",
                selected = uiState.preferredUnitSystem,
                options = listOf(UserPrefs.UnitSystem.METRIC, UserPrefs.UnitSystem.IMPERIAL),
                tagPrefix = "profile_units_",
                onOptionClick = onUnitSystemSelected,
            )

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = LightSurface1)) {
                Column(modifier = Modifier.padding(Spacing.cardInnerPadding), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text("Notifications", style = MaterialTheme.typography.titleSmall)
                    Text(if (uiState.notificationsEnabled) "Enabled" else "Disabled", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.testTag("profile_notifications"))
                }
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("profile_error"),
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    selected: String,
    options: List<String>,
    tagPrefix: String,
    onOptionClick: (String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = LightSurface1)) {
        Column(modifier = Modifier.padding(Spacing.cardInnerPadding), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                options.forEach { option ->
                    AssistChip(
                        onClick = { onOptionClick(option) },
                        label = { Text(option) },
                        modifier = Modifier.testTag("$tagPrefix$option"),
                    )
                }
            }
            Text("Current: $selected", style = MaterialTheme.typography.bodySmall)
        }
    }
}
