package com.fitforge.app.domain.usecase.workouts

import com.fitforge.app.data.local.db.entity.ExerciseEntity
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetWorkoutLibraryExercisesUseCaseTest {
    @Test
    fun `filters exercises by category and difficulty`() = runTest {
        val searchUseCase = mockk<SearchExercisesUseCase>()
        val push = exercise(id = "1", category = "Strength", difficulty = "Intermediate")
        val run = exercise(id = "2", category = "Cardio", difficulty = "Beginner")
        every { searchUseCase.invoke("bench") } returns flowOf(listOf(push, run))

        val result = GetWorkoutLibraryExercisesUseCase(searchUseCase)(
            query = "bench",
            selectedCategory = "Strength",
            selectedDifficulty = "Intermediate",
        ).first()

        assertEquals(listOf(push), result)
    }

    @Test
    fun `returns all query matches when no filters selected`() = runTest {
        val searchUseCase = mockk<SearchExercisesUseCase>()
        val push = exercise(id = "1", category = "Strength", difficulty = "Intermediate")
        val run = exercise(id = "2", category = "Cardio", difficulty = "Beginner")
        every { searchUseCase.invoke("") } returns flowOf(listOf(push, run))

        val result = GetWorkoutLibraryExercisesUseCase(searchUseCase)(
            query = "",
            selectedCategory = null,
            selectedDifficulty = null,
        ).first()

        assertEquals(2, result.size)
    }

    private fun exercise(id: String, category: String, difficulty: String): ExerciseEntity = ExerciseEntity(
        id = id,
        name = "Exercise $id",
        description = "desc",
        category = category,
        difficulty = difficulty,
        equipment = setOf("Dumbbell"),
        primaryMuscles = setOf("Chest"),
        secondaryMuscles = emptySet(),
        instructions = listOf("Step 1"),
        estimatedDurationSeconds = 900,
        estimatedCalories = 120,
        imageUrl = null,
        videoUrl = null,
        isPremium = false,
        source = "seed",
        createdAtEpochMillis = 1L,
        updatedAtEpochMillis = 1L,
    )
}
