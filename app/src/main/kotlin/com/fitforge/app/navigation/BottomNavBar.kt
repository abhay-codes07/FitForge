package com.fitforge.app.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.fitforge.app.presentation.theme.DarkOnSurfLow
import com.fitforge.app.presentation.theme.DarkSurface1
import com.fitforge.app.presentation.theme.IconSize
import com.fitforge.app.presentation.theme.LightOnSurfLow
import com.fitforge.app.presentation.theme.LightSurface0
import com.fitforge.app.presentation.theme.Purple400
import com.fitforge.app.presentation.theme.Purple600
import com.fitforge.app.presentation.theme.Spacing

@Composable
fun BottomNavBar(
    currentRoute: String,
    onRouteSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val darkTheme = MaterialTheme.colorScheme.background == DarkSurface1 ||
        MaterialTheme.colorScheme.surface == DarkSurface1
    val background = if (darkTheme) DarkSurface1 else LightSurface0
    val activeColor = if (darkTheme) Purple400 else Purple600
    val inactiveColor = if (darkTheme) DarkOnSurfLow else LightOnSurfLow
    val items = listOf(
        Triple(Screen.Home.route, "Home", Icons.Rounded.Home),
        Triple(Screen.Workouts.route, "Workouts", Icons.Rounded.FitnessCenter),
        Triple(Screen.Analytics.route, "Analytics", Icons.Rounded.Analytics),
        Triple(Screen.Profile.route, "Profile", Icons.Rounded.Person),
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.take(2).forEach { (route, label, icon) ->
            BottomNavItem(
                label = label,
                route = route,
                currentRoute = currentRoute,
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(IconSize.lg)) },
                onClick = { onRouteSelected(route) },
            )
        }

        FloatingActionButton(
            onClick = { onRouteSelected(Screen.StartWorkout.route) },
            shape = CircleShape,
            containerColor = Purple600,
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "Start workout",
                tint = Color.White,
                modifier = Modifier.size(IconSize.lg),
            )
        }

        items.drop(2).forEach { (route, label, icon) ->
            BottomNavItem(
                label = label,
                route = route,
                currentRoute = currentRoute,
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(IconSize.lg)) },
                onClick = { onRouteSelected(route) },
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    route: String,
    currentRoute: String,
    activeColor: Color,
    inactiveColor: Color,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    val selected = currentRoute == route
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.xs)
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(
            modifier = Modifier
                .size(width = Spacing.huge, height = Spacing.xxxs)
                .clip(MaterialTheme.shapes.small)
                .background(if (selected) activeColor else Color.Transparent),
        )
        Crossfade(targetState = selected, label = "bottomNavCrossfade") { isSelected ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = Spacing.xs),
            ) {
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center,
                ) {
                    CompositionLocalProvider(
                        androidx.compose.material3.LocalContentColor provides
                            if (isSelected) activeColor else inactiveColor,
                        content = icon,
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) activeColor else inactiveColor,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
