package com.fitforge.app.presentation.onboarding.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitforge.app.presentation.theme.Mint500
import com.fitforge.app.presentation.theme.Purple100
import com.fitforge.app.presentation.theme.Purple400
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Spacing

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun GoalSelectionScreen(
    uiState: GoalSelectionUiState,
    onGoalToggle: (String) -> Unit,
    onContinue: () -> Unit,
    onNavigateNext: (String) -> Unit,
) {
    LaunchedEffect(uiState.destinationRoute) {
        uiState.destinationRoute?.let(onNavigateNext)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("goal_selection_screen"),
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
                    text = "What do you want from FitForge?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Choose one or more goals. Your plan, progress signals, and recommendations will adapt to these choices.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    uiState.options.forEach { option ->
                        GoalChip(
                            option = option,
                            selected = option.id in uiState.selectedGoals,
                            onClick = { onGoalToggle(option.id) },
                        )
                    }
                }
            }

            Button(
                onClick = onContinue,
                enabled = uiState.canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("goal_continue"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple600,
                ),
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
private fun GoalChip(
    option: GoalOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val background = if (selected) Purple600 else Purple100
    val contentColor = if (selected) Mint500 else Purple600

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable(onClick = onClick)
            .testTag("goal_chip_${option.id}")
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = option.title,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = if (selected) MaterialTheme.colorScheme.onPrimary else contentColor,
            )
            Text(
                text = option.description,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) Purple400 else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(Spacing.huge))
                        .background(Mint500)
                        .align(Alignment.End),
                )
            }
        }
    }
}
