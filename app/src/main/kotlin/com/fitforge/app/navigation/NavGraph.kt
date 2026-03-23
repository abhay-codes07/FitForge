package com.fitforge.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitforge.app.presentation.onboarding.bodymetrics.BodyMetricsScreen
import com.fitforge.app.presentation.onboarding.bodymetrics.BodyMetricsViewModel
import com.fitforge.app.presentation.onboarding.fitnesslevel.FitnessLevelScreen
import com.fitforge.app.presentation.onboarding.fitnesslevel.FitnessLevelViewModel
import com.fitforge.app.presentation.onboarding.goals.GoalSelectionScreen
import com.fitforge.app.presentation.onboarding.goals.GoalSelectionViewModel
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
                onNavigateNext = { route ->
                    navController.navigate(route)
                },
            )
        }
        composable(Screen.GoalSelection.route) {
            val viewModel: GoalSelectionViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            GoalSelectionScreen(
                uiState = uiState.value,
                onGoalToggle = viewModel::onGoalToggle,
                onContinue = viewModel::onContinueClick,
                onNavigateNext = { route ->
                    navController.navigate(route)
                },
            )
        }
        composable(Screen.BodyMetrics.route) {
            val viewModel: BodyMetricsViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            BodyMetricsScreen(
                uiState = uiState.value,
                onUnitSystemSelected = viewModel::onUnitSystemSelected,
                onHeightChanged = viewModel::onHeightChanged,
                onWeightChanged = viewModel::onWeightChanged,
                onAgeChanged = viewModel::onAgeChanged,
                onGenderSelected = viewModel::onGenderSelected,
                onContinue = viewModel::onContinueClick,
                onNavigateNext = { route ->
                    navController.navigate(route)
                },
            )
        }
        composable(Screen.FitnessLevel.route) {
            val viewModel: FitnessLevelViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            FitnessLevelScreen(
                uiState = uiState.value,
                onFitnessLevelSelected = viewModel::onFitnessLevelSelected,
                onContinue = viewModel::onContinueClick,
                onNavigateNext = { },
            )
        }
    }
}
