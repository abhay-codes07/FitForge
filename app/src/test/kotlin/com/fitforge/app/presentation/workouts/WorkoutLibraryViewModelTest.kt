package com.fitforge.app.presentation.workouts

import com.fitforge.app.data.local.db.entity.ExerciseEntity
import com.fitforge.app.domain.usecase.workouts.GetWorkoutLibraryExercisesUseCase
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
class WorkoutLibraryViewModelTest {
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
    fun `loads exercises and maps values`() = runTest(dispatcher) {
        val useCase = mockk<GetWorkoutLibraryExercisesUseCase>()
        every { useCase.invoke(any(), any(), any()) } returns flowOf(
            listOf(
                exercise(id = "e1", name = "Bench Press", category = "Strength", difficulty = "Intermediate"),
            ),
        )

        val viewModel = WorkoutLibraryViewModel(useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(1, state.exercises.size)
        assertEquals("Bench Press", state.exercises.first().name)
    }

    @Test
    fun `updates query and category filters`() = runTest(dispatcher) {
        val useCase = mockk<GetWorkoutLibraryExercisesUseCase>()
        every { useCase.invoke(any(), any(), any()) } returns flowOf(emptyList())

        val viewModel = WorkoutLibraryViewModel(useCase)
        advanceUntilIdle()

        viewModel.onQueryChanged("run")
        viewModel.onCategorySelected("Cardio")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("run", state.searchQuery)
        assertEquals("Cardio", state.selectedCategory)
    }

    private fun exercise(
        id: String,
        name: String,
        category: String,
        difficulty: String,
    ): ExerciseEntity = ExerciseEntity(
        id = id,
        name = name,
        description = "desc",
        category = category,
        difficulty = difficulty,
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
