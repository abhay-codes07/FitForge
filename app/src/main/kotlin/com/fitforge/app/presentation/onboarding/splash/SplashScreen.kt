package com.fitforge.app.presentation.onboarding.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fitforge.app.presentation.theme.GradientDarkHero
import com.fitforge.app.presentation.theme.Mint500
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Spacing

@Composable
fun SplashScreen(
    uiState: SplashUiState,
    onSplashFinished: (String) -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "splashPulse")
    val logoScale by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "logoScale",
    )

    LaunchedEffect(uiState.destinationRoute) {
        uiState.destinationRoute?.let(onSplashFinished)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(GradientDarkHero))
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Spacing.xxl),
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .scale(logoScale)
                    .background(
                        brush = Brush.linearGradient(listOf(Purple600, Mint500)),
                        shape = CircleShape,
                    )
                    .testTag("splash_logo"),
            )
            Text(
                text = uiState.appName,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("splash_title"),
            )
            Text(
                text = uiState.tagline,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("splash_tagline"),
            )
        }
    }
}
