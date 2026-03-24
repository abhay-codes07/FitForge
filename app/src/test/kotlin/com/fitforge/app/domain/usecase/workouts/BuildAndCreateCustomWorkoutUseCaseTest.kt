package com.fitforge.app.domain.usecase.workouts

import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BuildAndCreateCustomWorkoutUseCaseTest {
    @Test
    fun `creates custom workout with selected exercises`() = runTest {
        val userRepository = mockk<UserRepository>()
        val createCustomWorkoutUseCase = mockk<CreateCustomWorkoutUseCase>()

        coEvery { userRepository.getPrimaryUser() } returns mockk<UserEntity> { every { id } returns "u1" }
        coEvery { createCustomWorkoutUseCase.invoke(any()) } returns Unit

        val useCase = BuildAndCreateCustomWorkoutUseCase(userRepository, createCustomWorkoutUseCase)
        val workoutId = useCase(
            CreateCustomWorkoutDraft(
                name = "Upper Body Blast",
                description = "Push and pull",
                workoutType = "Strength",
                difficulty = "Intermediate",
                estimatedDurationMinutes = 45,
                selectedExerciseIds = listOf("e1", "e2"),
            ),
        )

        assertEquals(false, workoutId.isBlank())
        coVerify(exactly = 1) { createCustomWorkoutUseCase.invoke(any()) }
    }

    @Test
    fun `throws when no exercises selected`() = runTest {
        val userRepository = mockk<UserRepository>()
        val createCustomWorkoutUseCase = mockk<CreateCustomWorkoutUseCase>()

        coEvery { userRepository.getPrimaryUser() } returns null

        var error: Throwable? = null
        try {
            BuildAndCreateCustomWorkoutUseCase(userRepository, createCustomWorkoutUseCase)(
                CreateCustomWorkoutDraft(
                    name = "Empty",
                    description = "",
                    workoutType = "Strength",
                    difficulty = "Beginner",
                    estimatedDurationMinutes = 30,
                    selectedExerciseIds = emptyList(),
                ),
            )
        } catch (t: Throwable) {
            error = t
        }

        assertEquals(IllegalArgumentException::class, error!!::class)
    }
}
