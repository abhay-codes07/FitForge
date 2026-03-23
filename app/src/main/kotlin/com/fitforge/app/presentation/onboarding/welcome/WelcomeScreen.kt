package com.fitforge.app.presentation.onboarding.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fitforge.app.presentation.theme.DarkSurface1
import com.fitforge.app.presentation.theme.LightSurface0
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Spacing

@Composable
fun WelcomeScreen(
    uiState: WelcomeUiState,
    onGetStarted: () -> Unit,
    onNavigateNext: (String) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { uiState.pages.size.coerceAtLeast(1) })

    LaunchedEffect(uiState.destinationRoute) {
        uiState.destinationRoute?.let(onNavigateNext)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("welcome_screen"),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.xl),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "FitForge",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.xl),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("welcome_pager"),
                ) { page ->
                    val pageData = uiState.pages.getOrNull(page)
                    if (pageData != null) {
                        WelcomePageCard(
                            page = pageData,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    repeat(uiState.pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .height(Spacing.xxxs)
                                .size(width = if (index == pagerState.currentPage) 28.dp else 10.dp, height = 10.dp)
                                .clip(RoundedCornerShape(Spacing.huge))
                                .background(
                                    if (index == pagerState.currentPage) Purple600
                                    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.18f),
                                )
                                .testTag("welcome_indicator_$index"),
                        )
                    }
                }
            }

            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welcome_get_started"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple600,
                    contentColor = LightSurface0,
                ),
            ) {
                Text(
                    text = "Get Started",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = Spacing.xxs),
                )
            }
        }
    }
}

@Composable
private fun WelcomePageCard(
    page: com.fitforge.app.domain.model.onboarding.WelcomePage,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = if (MaterialTheme.colorScheme.background == DarkSurface1) DarkSurface1 else LightSurface0,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(Spacing.xl))
                    .background(Brush.linearGradient(page.accentColors))
                    .testTag("welcome_hero"),
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        text = page.statValue,
                        style = MaterialTheme.typography.displaySmall,
                        color = LightSurface0,
                    )
                    Text(
                        text = page.statLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = LightSurface0.copy(alpha = 0.82f),
                    )
                }
            }

            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                textAlign = TextAlign.Start,
            )
        }
    }
}
