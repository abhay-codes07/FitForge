package com.fitforge.app.domain.usecase.active_workout

import com.fitforge.app.domain.repository.ExerciseSetRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class LogCompletedSetUseCaseTest {
    @Test
    fun `writes completed set into repository`() = runTest {
        val repository = mockk<ExerciseSetRepository>(relaxed = true)

        LogCompletedSetUseCase(repository)(
            workoutId = "w1",
            workoutExerciseId = "we1",
            exerciseId = "e1",
            setNumber = 1,
            reps = 12,
            weightKg = 20f,
            restSeconds = 60,
            completedAtEpochMillis = 100L,
        )

        coVerify(exactly = 1) { repository.upsertExerciseSet(any()) }
    }
}
