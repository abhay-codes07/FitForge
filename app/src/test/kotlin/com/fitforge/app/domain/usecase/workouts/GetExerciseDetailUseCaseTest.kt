package com.fitforge.app.domain.usecase.workouts

import com.fitforge.app.data.local.db.entity.ExerciseEntity
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class GetExerciseDetailUseCaseTest {
    @Test
    fun `maps exercise into detail data`() = runTest {
        val observeUseCase = mockk<ObserveExerciseDetailUseCase>()
        every { observeUseCase.invoke("e1") } returns flowOf(
            ExerciseEntity(
                id = "e1",
                name = "Bench Press",
                description = "Chest compound movement",
                category = "Strength",
                difficulty = "Intermediate",
                equipment = setOf("Barbell", "Bench"),
                primaryMuscles = setOf("Chest", "Triceps"),
                secondaryMuscles = emptySet(),
                instructions = listOf("Set grip", "Lower bar", "Press up"),
                estimatedDurationSeconds = 900,
                estimatedCalories = 120,
                imageUrl = null,
                videoUrl = null,
                isPremium = false,
                source = "seed",
                createdAtEpochMillis = 1L,
                updatedAtEpochMillis = 1L,
            ),
        )

        val result = GetExerciseDetailUseCase(observeUseCase)("e1").first()

        assertNotNull(result)
        assertEquals("Bench Press", result?.name)
        assertEquals("15 min", result?.durationLabel)
        assertEquals("120 kcal", result?.caloriesLabel)
    }

    @Test
    fun `returns null when exercise is missing`() = runTest {
        val observeUseCase = mockk<ObserveExerciseDetailUseCase>()
        every { observeUseCase.invoke("missing") } returns flowOf(null)

        val result = GetExerciseDetailUseCase(observeUseCase)("missing").first()
        assertEquals(null, result)
    }
}
