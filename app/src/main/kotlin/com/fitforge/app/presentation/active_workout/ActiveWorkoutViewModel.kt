package com.fitforge.app.presentation.active_workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.active_workout.ActiveWorkoutExerciseStep
import com.fitforge.app.domain.usecase.active_workout.ActiveWorkoutSessionData
import com.fitforge.app.domain.usecase.active_workout.CompleteWorkoutUseCase
import com.fitforge.app.domain.usecase.active_workout.GetActiveWorkoutSessionUseCase
import com.fitforge.app.domain.usecase.active_workout.LogCompletedSetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val getActiveWorkoutSessionUseCase: GetActiveWorkoutSessionUseCase,
    private val logCompletedSetUseCase: LogCompletedSetUseCase,
    private val completeWorkoutUseCase: CompleteWorkoutUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ActiveWorkoutUiState())
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    private var session: ActiveWorkoutSessionData? = null
    private var completedSetCounts: MutableMap<String, Int> = mutableMapOf()
    private var restTimerJob: Job? = null

    init {
        load()
    }

    fun onRepIncrement() {
        _uiState.update { it.copy(repCount = it.repCount + 1) }
    }

    fun onRepDecrement() {
        _uiState.update { state ->
            state.copy(repCount = (state.repCount - 1).coerceAtLeast(0))
        }
    }

    fun onShuffleExercise() {
        val currentSession = session ?: return
        if (currentSession.exercises.size < 2) {
            return
        }

        val currentIndex = _uiState.value.currentExerciseIndex
        val nextIndex = currentSession.exercises.indices
            .filterNot { it == currentIndex }
            .random(Random(System.currentTimeMillis()))

        applyExercise(index = nextIndex, resetRepCount = true)
    }

    fun onSkipExercise() {
        moveToNextExerciseOrCompleteWorkout(startRestTimer = false)
    }

    fun onCompleteSet(weightKg: Float? = null) {
        val currentSession = session ?: return
        val state = _uiState.value
        val step = currentSession.exercises.getOrNull(state.currentExerciseIndex) ?: return
        val setNumber = state.currentSet
        val reps = state.repCount.takeIf { it > 0 } ?: step.targetReps

        viewModelScope.launch {
            runCatching {
                logCompletedSetUseCase(
                    workoutId = currentSession.workoutId,
                    workoutExerciseId = step.workoutExerciseId,
                    exerciseId = step.exerciseId,
                    setNumber = setNumber,
                    reps = reps,
                    weightKg = weightKg,
                    restSeconds = step.restSeconds,
                )
            }.onSuccess {
                val completedForExercise = (completedSetCounts[step.workoutExerciseId] ?: 0) + 1
                completedSetCounts[step.workoutExerciseId] = completedForExercise

                if (completedForExercise >= step.targetSets) {
                    moveToNextExerciseOrCompleteWorkout(startRestTimer = true)
                } else {
                    _uiState.update {
                        it.copy(
                            currentSet = completedForExercise + 1,
                            repCount = 0,
                            restSecondsRemaining = step.restSeconds,
                        )
                    }
                    startRestTimer(step.restSeconds)
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "Failed to save set")
                }
            }
        }
    }

    fun onDismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onDismissRestTimer() {
        restTimerJob?.cancel()
        _uiState.update { it.copy(restSecondsRemaining = 0) }
    }

    fun retry() {
        load()
    }

    private fun load() {
        restTimerJob?.cancel()
        viewModelScope.launch {
            _uiState.value = ActiveWorkoutUiState(isLoading = true)
            runCatching { getActiveWorkoutSessionUseCase() }
                .onSuccess { loadedSession ->
                    session = loadedSession
                    completedSetCounts = loadedSession.completedSetCounts.toMutableMap()
                    applyExercise(index = loadedSession.currentExerciseIndex, resetRepCount = true)
                }
                .onFailure { throwable ->
                    _uiState.value = ActiveWorkoutUiState(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load active workout",
                    )
                }
        }
    }

    private fun moveToNextExerciseOrCompleteWorkout(startRestTimer: Boolean) {
        val currentSession = session ?: return
        val currentIndex = _uiState.value.currentExerciseIndex

        if (currentIndex < currentSession.exercises.lastIndex) {
            val currentStep = currentSession.exercises[currentIndex]
            val nextStep = currentSession.exercises[currentIndex + 1]
            applyExercise(index = currentIndex + 1, resetRepCount = true)
            if (startRestTimer) {
                _uiState.update { it.copy(restSecondsRemaining = currentStep.restSeconds) }
                startRestTimer(currentStep.restSeconds)
            } else {
                _uiState.update { it.copy(restSecondsRemaining = 0) }
            }
            val completedForNext = completedSetCounts[nextStep.workoutExerciseId] ?: 0
            _uiState.update { it.copy(currentSet = completedForNext + 1) }
            return
        }

        viewModelScope.launch {
            runCatching {
                completeWorkoutUseCase(currentSession.workoutId)
            }.onSuccess {
                restTimerJob?.cancel()
                _uiState.update {
                    it.copy(
                        isWorkoutCompleted = true,
                        restSecondsRemaining = 0,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "Failed to complete workout")
                }
            }
        }
    }

    private fun applyExercise(index: Int, resetRepCount: Boolean) {
        val currentSession = session ?: return
        val step = currentSession.exercises.getOrNull(index) ?: return
        val completedForStep = completedSetCounts[step.workoutExerciseId] ?: 0

        _uiState.update { state ->
            state.copy(
                isLoading = false,
                errorMessage = null,
                workoutTitle = currentSession.workoutTitle,
                exerciseName = step.exerciseName,
                exerciseImageUrl = step.imageUrl,
                currentExerciseIndex = index,
                totalExercises = currentSession.exercises.size,
                currentSet = (completedForStep + 1).coerceAtMost(step.targetSets),
                targetSets = step.targetSets,
                repCount = if (resetRepCount) 0 else state.repCount,
                targetReps = step.targetReps,
                restSecondsRemaining = 0,
            )
        }
    }

    private fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        if (seconds <= 0) {
            _uiState.update { it.copy(restSecondsRemaining = 0) }
            return
        }

        restTimerJob = viewModelScope.launch {
            for (remaining in seconds downTo 1) {
                _uiState.update { it.copy(restSecondsRemaining = remaining) }
                delay(1000)
            }
            _uiState.update { it.copy(restSecondsRemaining = 0) }
        }
    }
}
