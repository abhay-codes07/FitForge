package com.fitforge.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitforge.app.presentation.onboarding.splash.SplashScreen
import com.fitforge.app.presentation.onboarding.splash.SplashViewModel
import com.fitforge.app.presentation.onboarding.welcome.WelcomeScreen
import com.fitforge.app.presentation.onboarding.welcome.WelcomeViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
    ) {
        composable(Screen.Splash.route) {
            val viewModel: SplashViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            SplashScreen(
                uiState = uiState.value,
                onSplashFinished = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.Welcome.route) {
            val viewModel: WelcomeViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            WelcomeScreen(
                uiState = uiState.value,
                onGetStarted = viewModel::onGetStartedClick,
                onNavigateNext = { },
            )
        }
    }
}
