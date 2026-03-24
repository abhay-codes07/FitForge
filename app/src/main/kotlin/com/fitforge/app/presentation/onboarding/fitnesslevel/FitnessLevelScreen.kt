package com.fitforge.app.presentation.onboarding.fitnesslevel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitforge.app.domain.usecase.onboarding.FitnessLevelOption
import com.fitforge.app.presentation.theme.Mint500
import com.fitforge.app.presentation.theme.Purple100
import com.fitforge.app.presentation.theme.Purple400
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Spacing

@Composable
fun FitnessLevelScreen(
    uiState: FitnessLevelUiState,
    onFitnessLevelSelected: (String) -> Unit,
    onContinue: () -> Unit,
    onNavigateNext: (String) -> Unit,
) {
    LaunchedEffect(uiState.destinationRoute) {
        uiState.destinationRoute?.let(onNavigateNext)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("fitness_level_screen"),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.xl),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Text(
                    text = "How experienced are you?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Pick the level that best matches your current training base. FitForge will tune intensity, volume, and recovery around this.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                )
                Column(
                    modifier = Modifier.padding(top = Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    uiState.options.forEach { option ->
                        FitnessLevelCard(
                            option = option,
                            selected = uiState.selectedLevel == option.storageValue,
                            onClick = { onFitnessLevelSelected(option.storageValue) },
                        )
                    }
                }
            }

            Button(
                onClick = onContinue,
                enabled = uiState.canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fitness_level_continue"),
                colors = ButtonDefaults.buttonColors(containerColor = Purple600),
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = Spacing.xxs),
                )
            }
        }
    }
}

@Composable
private fun FitnessLevelCard(
    option: FitnessLevelOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (selected) Purple600 else Purple100

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("fitness_level_${option.storageValue}"),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = option.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = if (selected) MaterialTheme.colorScheme.onPrimary else Purple600,
            )
            Text(
                text = option.description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) Purple400 else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.76f),
            )
            if (selected) {
                Text(
                    text = "Selected",
                    style = MaterialTheme.typography.labelMedium,
                    color = Mint500,
                )
            }
        }
    }
}
