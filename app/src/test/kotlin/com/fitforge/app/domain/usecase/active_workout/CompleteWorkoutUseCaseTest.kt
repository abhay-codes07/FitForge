package com.fitforge.app.domain.usecase.active_workout

import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.domain.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CompleteWorkoutUseCaseTest {
    @Test
    fun `marks workout as completed`() = runTest {
        val repository = mockk<WorkoutRepository>()
        val workout = WorkoutEntity(
            id = "w1",
            userId = "u1",
            name = "Workout",
            description = "",
            workoutType = "strength",
            difficulty = "beginner",
            estimatedDurationMinutes = 30,
            estimatedCalories = 200,
            source = "custom",
            scheduledDateEpochMillis = null,
            completedAtEpochMillis = null,
            status = "planned",
            notes = null,
            createdAtEpochMillis = 1L,
            updatedAtEpochMillis = 1L,
        )

        coEvery { repository.getWorkout("w1") } returns workout
        coEvery { repository.upsertWorkout(any()) } returns Unit

        CompleteWorkoutUseCase(repository)("w1", nowEpochMillis = 999L)

        coVerify(exactly = 1) {
            repository.upsertWorkout(withArg {
                assert(it.status == "completed")
                assert(it.completedAtEpochMillis == 999L)
            })
        }
    }
}
