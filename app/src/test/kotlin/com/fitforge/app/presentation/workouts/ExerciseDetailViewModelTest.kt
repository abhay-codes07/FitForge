package com.fitforge.app.presentation.workouts

import androidx.lifecycle.SavedStateHandle
import com.fitforge.app.domain.usecase.workouts.ExerciseDetailData
import com.fitforge.app.domain.usecase.workouts.GetExerciseDetailUseCase
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
class ExerciseDetailViewModelTest {
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
    fun `loads exercise details on init`() = runTest(dispatcher) {
        val useCase = mockk<GetExerciseDetailUseCase>()
        every { useCase.invoke("e1") } returns flowOf(
            ExerciseDetailData(
                id = "e1",
                name = "Bench Press",
                description = "Chest movement",
                category = "Strength",
                difficulty = "Intermediate",
                durationLabel = "15 min",
                caloriesLabel = "120 kcal",
                equipment = listOf("Barbell"),
                primaryMuscles = listOf("Chest"),
                instructions = listOf("Set grip", "Press"),
            ),
        )

        val viewModel = ExerciseDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("exerciseId" to "e1")),
            getExerciseDetailUseCase = useCase,
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals("Bench Press", state.title)
        assertEquals(2, state.instructions.size)
    }

    @Test
    fun `shows not found when detail is missing`() = runTest(dispatcher) {
        val useCase = mockk<GetExerciseDetailUseCase>()
        every { useCase.invoke("missing") } returns flowOf(null)

        val viewModel = ExerciseDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("exerciseId" to "missing")),
            getExerciseDetailUseCase = useCase,
        )
        advanceUntilIdle()

        assertEquals("Exercise not found", viewModel.uiState.value.errorMessage)
    }
}
