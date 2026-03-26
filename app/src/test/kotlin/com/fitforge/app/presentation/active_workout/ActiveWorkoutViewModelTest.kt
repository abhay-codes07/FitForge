package com.fitforge.app.presentation.active_workout

import com.fitforge.app.domain.usecase.active_workout.ActiveWorkoutExerciseStep
import com.fitforge.app.domain.usecase.active_workout.ActiveWorkoutSessionData
import com.fitforge.app.domain.usecase.active_workout.CompleteWorkoutUseCase
import com.fitforge.app.domain.usecase.active_workout.GetActiveWorkoutSessionUseCase
import com.fitforge.app.domain.usecase.active_workout.LogCompletedSetUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveWorkoutViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads active workout session on init`() = runTest(dispatcher) {
        val getUseCase = mockk<GetActiveWorkoutSessionUseCase>()
        val logUseCase = mockk<LogCompletedSetUseCase>(relaxed = true)
        val completeUseCase = mockk<CompleteWorkoutUseCase>(relaxed = true)

        coEvery { getUseCase.invoke(any()) } returns sessionData()

        val viewModel = ActiveWorkoutViewModel(getUseCase, logUseCase, completeUseCase)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Squat", viewModel.uiState.value.exerciseName)
        assertEquals(2, viewModel.uiState.value.totalExercises)
    }

    @Test
    fun `rep controls update counter`() = runTest(dispatcher) {
        val getUseCase = mockk<GetActiveWorkoutSessionUseCase>()
        val logUseCase = mockk<LogCompletedSetUseCase>(relaxed = true)
        val completeUseCase = mockk<CompleteWorkoutUseCase>(relaxed = true)

        coEvery { getUseCase.invoke(any()) } returns sessionData()

        val viewModel = ActiveWorkoutViewModel(getUseCase, logUseCase, completeUseCase)
        advanceUntilIdle()

        viewModel.onRepIncrement()
        viewModel.onRepIncrement()
        viewModel.onRepDecrement()

        assertEquals(1, viewModel.uiState.value.repCount)
    }

    @Test
    fun `completing set progresses workout and finishes at end`() = runTest(dispatcher) {
        val getUseCase = mockk<GetActiveWorkoutSessionUseCase>()
        val logUseCase = mockk<LogCompletedSetUseCase>(relaxed = true)
        val completeUseCase = mockk<CompleteWorkoutUseCase>(relaxed = true)

        coEvery { getUseCase.invoke(any()) } returns sessionData()

        val viewModel = ActiveWorkoutViewModel(getUseCase, logUseCase, completeUseCase)
        advanceUntilIdle()

        viewModel.onCompleteSet()
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.currentExerciseIndex)

        viewModel.onCompleteSet()
        advanceUntilIdle()

        coVerify(exactly = 2) { logUseCase.invoke(any(), any(), any(), any(), any(), any(), any(), any()) }
        coVerify(exactly = 1) { completeUseCase.invoke("w1", any()) }
        assertTrue(viewModel.uiState.value.isWorkoutCompleted)
    }

    @Test
    fun `completing intermediate set starts rest timer and advances set count`() = runTest(dispatcher) {
        val getUseCase = mockk<GetActiveWorkoutSessionUseCase>()
        val logUseCase = mockk<LogCompletedSetUseCase>(relaxed = true)
        val completeUseCase = mockk<CompleteWorkoutUseCase>(relaxed = true)

        coEvery { getUseCase.invoke(any()) } returns multiSetSessionData()

        val viewModel = ActiveWorkoutViewModel(getUseCase, logUseCase, completeUseCase)
        advanceUntilIdle()

        viewModel.onCompleteSet()
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.currentSet)
        assertTrue(viewModel.uiState.value.restSecondsRemaining >= 0)
    }

    @Test
    fun `dismiss rest timer clears remaining seconds`() = runTest(dispatcher) {
        val getUseCase = mockk<GetActiveWorkoutSessionUseCase>()
        val logUseCase = mockk<LogCompletedSetUseCase>(relaxed = true)
        val completeUseCase = mockk<CompleteWorkoutUseCase>(relaxed = true)

        coEvery { getUseCase.invoke(any()) } returns multiSetSessionData()

        val viewModel = ActiveWorkoutViewModel(getUseCase, logUseCase, completeUseCase)
        advanceUntilIdle()

        viewModel.onCompleteSet()
        advanceUntilIdle()
        viewModel.onDismissRestTimer()

        assertEquals(0, viewModel.uiState.value.restSecondsRemaining)
    }

    private fun sessionData(): ActiveWorkoutSessionData = ActiveWorkoutSessionData(
        workoutId = "w1",
        workoutTitle = "Leg Day",
        exercises = listOf(
            ActiveWorkoutExerciseStep(
                workoutExerciseId = "we1",
                exerciseId = "e1",
                exerciseName = "Squat",
                imageUrl = null,
                targetSets = 1,
                targetReps = 10,
                restSeconds = 30,
            ),
            ActiveWorkoutExerciseStep(
                workoutExerciseId = "we2",
                exerciseId = "e2",
                exerciseName = "Lunge",
                imageUrl = null,
                targetSets = 1,
                targetReps = 12,
                restSeconds = 30,
            ),
        ),
        currentExerciseIndex = 0,
        completedSetCounts = emptyMap(),
    )

    private fun multiSetSessionData(): ActiveWorkoutSessionData = ActiveWorkoutSessionData(
        workoutId = "w2",
        workoutTitle = "Strength",
        exercises = listOf(
            ActiveWorkoutExerciseStep(
                workoutExerciseId = "we10",
                exerciseId = "e10",
                exerciseName = "Bench Press",
                imageUrl = null,
                targetSets = 2,
                targetReps = 8,
                restSeconds = 30,
            ),
        ),
        currentExerciseIndex = 0,
        completedSetCounts = emptyMap(),
    )
}

