package com.fitforge.app.presentation.workouts

import com.fitforge.app.data.local.db.entity.ExerciseEntity
import com.fitforge.app.domain.usecase.workouts.BuildAndCreateCustomWorkoutUseCase
import com.fitforge.app.domain.usecase.workouts.SearchExercisesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateCustomWorkoutViewModelTest {
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
    fun `loads exercises and toggles selection`() = runTest(dispatcher) {
        val searchUseCase = mockk<SearchExercisesUseCase>()
        val createUseCase = mockk<BuildAndCreateCustomWorkoutUseCase>()

        every { searchUseCase.invoke(any()) } returns flowOf(
            listOf(
                exercise("e1", "Bench Press"),
                exercise("e2", "Rows"),
            ),
        )
        coEvery { createUseCase.invoke(any()) } returns "w1"

        val viewModel = CreateCustomWorkoutViewModel(searchUseCase, createUseCase)
        advanceUntilIdle()

        viewModel.onExerciseToggled("e1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(2, state.exercises.size)
        assertEquals(true, state.selectedExerciseIds.contains("e1"))
    }

    @Test
    fun `creates workout on save`() = runTest(dispatcher) {
        val searchUseCase = mockk<SearchExercisesUseCase>()
        val createUseCase = mockk<BuildAndCreateCustomWorkoutUseCase>()

        every { searchUseCase.invoke(any()) } returns flowOf(listOf(exercise("e1", "Bench Press")))
        coEvery { createUseCase.invoke(any()) } returns "w123"

        val viewModel = CreateCustomWorkoutViewModel(searchUseCase, createUseCase)
        advanceUntilIdle()

        viewModel.onNameChanged("Upper Day")
        viewModel.onExerciseToggled("e1")
        viewModel.onSaveClick()
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isSaving)
        assertEquals(true, viewModel.uiState.value.successMessage?.contains("w123") == true)
    }

    private fun exercise(id: String, name: String): ExerciseEntity = ExerciseEntity(
        id = id,
        name = name,
        description = "desc",
        category = "Strength",
        difficulty = "Beginner",
        equipment = setOf("Dumbbell"),
        primaryMuscles = setOf("Chest"),
        secondaryMuscles = emptySet(),
        instructions = listOf("Step 1"),
        estimatedDurationSeconds = 600,
        estimatedCalories = 100,
        imageUrl = null,
        videoUrl = null,
        isPremium = false,
        source = "seed",
        createdAtEpochMillis = 1L,
        updatedAtEpochMillis = 1L,
    )
}
