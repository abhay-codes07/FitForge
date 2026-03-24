package com.fitforge.app.domain.usecase.active_workout

import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetStartWorkoutOptionsUseCaseTest {
    @Test
    fun `returns default options when no user`() = runTest {
        val userRepo = mockk<UserRepository>()
        val workoutRepo = mockk<WorkoutRepository>()
        coEvery { userRepo.getPrimaryUser() } returns null

        val result = GetStartWorkoutOptionsUseCase(userRepo, workoutRepo)(nowEpochMillis = 1000L)

        assertEquals(false, result.hasPrimaryUser)
        assertEquals(4, result.options.size)
    }

    @Test
    fun `includes scheduled option when workouts exist`() = runTest {
        val userRepo = mockk<UserRepository>()
        val workoutRepo = mockk<WorkoutRepository>()
        val user = mockk<UserEntity> { every { id } returns "u1" }
        val workout = mockk<WorkoutEntity>()

        coEvery { userRepo.getPrimaryUser() } returns user
        every { workoutRepo.observeScheduledWorkoutsForRange("u1", any(), any()) } returns flowOf(listOf(workout))

        val result = GetStartWorkoutOptionsUseCase(userRepo, workoutRepo)(nowEpochMillis = 1000L)

        assertEquals(true, result.hasPrimaryUser)
        assertEquals(1, result.scheduledWorkoutCount)
        assertEquals("Start Scheduled Workout", result.options.first())
    }
}
