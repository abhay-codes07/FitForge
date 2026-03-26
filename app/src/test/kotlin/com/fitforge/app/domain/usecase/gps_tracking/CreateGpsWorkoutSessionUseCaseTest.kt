package com.fitforge.app.domain.usecase.gps_tracking

import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CreateGpsWorkoutSessionUseCaseTest {
    @Test
    fun `creates in progress workout for selected mode`() = runTest {
        val userRepository = mockk<UserRepository>()
        val workoutRepository = mockk<WorkoutRepository>(relaxed = true)
        val user = mockk<UserEntity> { every { id } returns "u1" }

        coEvery { userRepository.getPrimaryUser() } returns user

        val useCase = CreateGpsWorkoutSessionUseCase(userRepository, workoutRepository)
        val workoutId = useCase(mode = "Run", nowEpochMillis = 123L)

        assertEquals(36, workoutId.length)
        coVerify(exactly = 1) {
            workoutRepository.upsertWorkout(withArg {
                assert(it.userId == "u1")
                assert(it.status == "in_progress")
                assert(it.workoutType == "run")
            })
        }
    }
}
