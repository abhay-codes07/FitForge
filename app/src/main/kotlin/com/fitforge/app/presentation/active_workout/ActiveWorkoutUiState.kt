package com.fitforge.app.presentation.active_workout

data class ActiveWorkoutUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val workoutTitle: String = "",
    val exerciseName: String = "",
    val exerciseImageUrl: String? = null,
    val currentExerciseIndex: Int = 0,
    val totalExercises: Int = 0,
    val currentSet: Int = 1,
    val targetSets: Int = 0,
    val repCount: Int = 0,
    val targetReps: Int = 0,
    val restSecondsRemaining: Int = 0,
    val isWorkoutCompleted: Boolean = false,
)
