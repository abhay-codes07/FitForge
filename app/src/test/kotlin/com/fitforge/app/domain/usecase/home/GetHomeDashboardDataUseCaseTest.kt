package com.fitforge.app.domain.usecase.home

import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import com.fitforge.app.data.local.db.entity.DailyLogEntity
import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.domain.repository.BodyMeasurementRepository
import com.fitforge.app.domain.repository.DailyLogRepository
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.util.TimeZone
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetHomeDashboardDataUseCaseTest {
    private val userRepository = mockk<UserRepository>()
    private val dailyLogRepository = mockk<DailyLogRepository>()
    private val workoutRepository = mockk<WorkoutRepository>()
    private val bodyMeasurementRepository = mockk<BodyMeasurementRepository>()

    private val useCase = GetHomeDashboardDataUseCase(
        userRepository = userRepository,
        dailyLogRepository = dailyLogRepository,
        workoutRepository = workoutRepository,
        bodyMeasurementRepository = bodyMeasurementRepository,
    )

    @Test
    fun `returns guest dashboard when no primary user exists`() = runTest {
        coEvery { userRepository.getPrimaryUser() } returns null

        val result = useCase(nowEpochMillis = 1735689600000, timezone = TimeZone.getTimeZone("UTC"))

        assertEquals(true, result.isGuest)
        assertEquals("Athlete", result.greetingName)
        assertEquals(0, result.steps)
        assertEquals(0, result.recentWorkoutTitles.size)
    }

    @Test
    fun `maps dashboard data for primary user`() = runTest {
        val user = mockk<UserEntity> {
            every { id } returns "u1"
            every { displayName } returns "Abhay"
        }
        val dailyLog = mockk<DailyLogEntity> {
            every { steps } returns 9000
            every { activeCalories } returns 510
            every { workoutMinutes } returns 62
            every { waterIntakeMl } returns 2400
        }
        val workout = mockk<WorkoutEntity> { every { name } returns "Upper Body Strength" }
        val measurement = mockk<BodyMeasurementEntity> { every { weightKg } returns 78.4f }

        coEvery { userRepository.getPrimaryUser() } returns user
        coEvery { dailyLogRepository.getLogForDay("u1", any()) } returns dailyLog
        every { workoutRepository.observeRecentCompletedWorkouts("u1", 5) } returns flowOf(listOf(workout))
        every { workoutRepository.observeNextScheduledWorkout("u1", any(), any()) } returns flowOf(workout)
        coEvery { bodyMeasurementRepository.getLatestMeasurement("u1") } returns measurement

        val result = useCase(nowEpochMillis = 1735689600000, timezone = TimeZone.getTimeZone("UTC"))

        assertEquals(false, result.isGuest)
        assertEquals("Abhay", result.greetingName)
        assertEquals(9000, result.steps)
        assertEquals("Upper Body Strength", result.nextWorkoutTitle)
        assertEquals(1, result.recentWorkoutTitles.size)
        assertEquals(78.4f, result.latestWeightKg)
    }
}
