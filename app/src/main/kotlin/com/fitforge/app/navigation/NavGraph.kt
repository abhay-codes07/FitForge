package com.fitforge.app.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitforge.app.presentation.home.HomeDashboardScreen
import com.fitforge.app.presentation.home.HomeDashboardViewModel
import com.fitforge.app.presentation.onboarding.auth.AuthScreen
import com.fitforge.app.presentation.onboarding.auth.AuthViewModel
import com.fitforge.app.presentation.onboarding.bodymetrics.BodyMetricsScreen
import com.fitforge.app.presentation.onboarding.bodymetrics.BodyMetricsViewModel
import com.fitforge.app.presentation.onboarding.fitnesslevel.FitnessLevelScreen
import com.fitforge.app.presentation.onboarding.fitnesslevel.FitnessLevelViewModel
import com.fitforge.app.presentation.onboarding.goals.GoalSelectionScreen
import com.fitforge.app.presentation.onboarding.goals.GoalSelectionViewModel
import com.fitforge.app.presentation.onboarding.permissions.PermissionsScreen
import com.fitforge.app.presentation.onboarding.permissions.PermissionsViewModel
import com.fitforge.app.presentation.onboarding.schedulesetup.ScheduleSetupScreen
import com.fitforge.app.presentation.onboarding.schedulesetup.ScheduleSetupViewModel
import com.fitforge.app.presentation.onboarding.splash.SplashScreen
import com.fitforge.app.presentation.onboarding.splash.SplashViewModel
import com.fitforge.app.presentation.onboarding.welcome.WelcomeScreen
import com.fitforge.app.presentation.onboarding.welcome.WelcomeViewModel
import com.fitforge.app.presentation.onboarding.workoutpreferences.WorkoutPreferencesScreen
import com.fitforge.app.presentation.onboarding.workoutpreferences.WorkoutPreferencesViewModel
import com.fitforge.app.presentation.workouts.WorkoutLibraryScreen
import com.fitforge.app.presentation.workouts.WorkoutLibraryViewModel

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
                onNavigateNext = { route -> navController.navigate(route) },
            )
        }
        composable(Screen.GoalSelection.route) {
            val viewModel: GoalSelectionViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            GoalSelectionScreen(
                uiState = uiState.value,
                onGoalToggle = viewModel::onGoalToggle,
                onContinue = viewModel::onContinueClick,
                onNavigateNext = { route -> navController.navigate(route) },
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
                onNavigateNext = { route -> navController.navigate(route) },
            )
        }
        composable(Screen.FitnessLevel.route) {
            val viewModel: FitnessLevelViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            FitnessLevelScreen(
                uiState = uiState.value,
                onFitnessLevelSelected = viewModel::onFitnessLevelSelected,
                onContinue = viewModel::onContinueClick,
                onNavigateNext = { route -> navController.navigate(route) },
            )
        }
        composable(Screen.WorkoutPreferences.route) {
            val viewModel: WorkoutPreferencesViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            WorkoutPreferencesScreen(
                uiState = uiState.value,
                onLocationToggle = viewModel::onLocationToggle,
                onEquipmentToggle = viewModel::onEquipmentToggle,
                onContinue = viewModel::onContinueClick,
                onNavigateNext = { route -> navController.navigate(route) },
            )
        }
        composable(Screen.ScheduleSetup.route) {
            val viewModel: ScheduleSetupViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            ScheduleSetupScreen(
                uiState = uiState.value,
                onDayToggle = viewModel::onDayToggle,
                onDurationChanged = viewModel::onDurationChanged,
                onContinue = viewModel::onContinueClick,
                onNavigateNext = { route -> navController.navigate(route) },
            )
        }
        composable(Screen.Permissions.route) {
            val viewModel: PermissionsViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            PermissionsScreen(
                uiState = uiState.value,
                onHealthConnectAvailabilityResolved = viewModel::setHealthConnectAvailability,
                onNotificationPermissionResult = viewModel::onNotificationPermissionResult,
                onNotificationSkipped = viewModel::onNotificationSkipped,
                onHealthConnectPermissionResult = viewModel::onHealthConnectPermissionResult,
                onHealthConnectSkipped = viewModel::onHealthConnectSkipped,
                onContinue = viewModel::onContinueClick,
                onNavigateNext = { route -> navController.navigate(route) },
            )
        }
        composable(Screen.Auth.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            AuthScreen(
                uiState = uiState.value,
                onEmailChanged = viewModel::onEmailChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onEmailContinue = viewModel::onEmailContinueClick,
                onGoogleContinue = viewModel::onGoogleContinueClick,
                onGuestContinue = viewModel::onGuestContinueClick,
                onNavigateNext = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.Home.route) {
            val viewModel: HomeDashboardViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            HomeDashboardScreen(
                uiState = uiState.value,
                onRefresh = viewModel::refresh,
            )
        }
        composable(Screen.Workouts.route) {
            val viewModel: WorkoutLibraryViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            WorkoutLibraryScreen(
                uiState = uiState.value,
                onQueryChanged = viewModel::onQueryChanged,
                onCategorySelected = viewModel::onCategorySelected,
                onDifficultySelected = viewModel::onDifficultySelected,
            )
        }
    }
}
