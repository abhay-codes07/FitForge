package com.fitforge.app.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Welcome : Screen("welcome")
    data object GoalSelection : Screen("goal_selection")
    data object BodyMetrics : Screen("body_metrics")
    data object FitnessLevel : Screen("fitness_level")
    data object WorkoutPreferences : Screen("workout_preferences")
    data object ScheduleSetup : Screen("schedule_setup")
    data object Permissions : Screen("permissions")
    data object Auth : Screen("auth")
    data object Home : Screen("home")
    data object Workouts : Screen("workouts")
    data object CustomWorkout : Screen("custom_workout")
    data object ExerciseDetail : Screen("exercise_detail/{exerciseId}") {
        fun createRoute(exerciseId: String): String = "exercise_detail/$exerciseId"
    }
    data object Analytics : Screen("analytics")
    data object BodyProgress : Screen("body_progress")
    data object StartWorkout : Screen("start_workout")
    data object ActiveWorkout : Screen("active_workout")
    data object WorkoutSummary : Screen("workout_summary")
    data object GpsTracking : Screen("gps_tracking")
    data object Profile : Screen("profile")
    data object Achievements : Screen("achievements")
    data object Challenges : Screen("challenges")
    data object Programs : Screen("programs")
    data object DailyLog : Screen("daily_log")
    data object WorkoutHistory : Screen("workout_history")
    data object Social : Screen("social")
    data object Nutrition : Screen("nutrition")
    data object Templates : Screen("templates")
    data object Recovery : Screen("recovery")
}
