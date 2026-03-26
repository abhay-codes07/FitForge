package com.fitforge.app.domain.usecase.gps_tracking

import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.domain.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CompleteGpsWorkoutSessionUseCaseTest {
    @Test
    fun `marks workout completed and stores distance note`() = runTest {
        val workoutRepository = mockk<WorkoutRepository>()
        val workout = WorkoutEntity(
            id = "w1",
            userId = "u1",
            name = "Run Session",
            description = "",
            workoutType = "run",
            difficulty = "beginner",
            estimatedDurationMinutes = 0,
            estimatedCalories = 0,
            source = "gps",
            scheduledDateEpochMillis = null,
            completedAtEpochMillis = null,
            status = "in_progress",
            notes = null,
            createdAtEpochMillis = 1L,
            updatedAtEpochMillis = 1L,
        )

        coEvery { workoutRepository.getWorkout("w1") } returns workout
        coEvery { workoutRepository.upsertWorkout(any()) } returns Unit

        CompleteGpsWorkoutSessionUseCase(workoutRepository)(
            workoutId = "w1",
            durationSeconds = 1800,
            distanceMeters = 5300f,
            nowEpochMillis = 999L,
        )

        coVerify(exactly = 1) {
            workoutRepository.upsertWorkout(withArg {
                assert(it.status == "completed")
                assert(it.estimatedDurationMinutes == 30)
                assert(it.notes?.contains("5.30") == true)
            })
        }
    }
}
