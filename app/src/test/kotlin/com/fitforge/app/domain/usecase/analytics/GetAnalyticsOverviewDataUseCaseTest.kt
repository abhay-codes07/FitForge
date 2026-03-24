package com.fitforge.app.domain.usecase.analytics

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

class GetAnalyticsOverviewDataUseCaseTest {
    @Test
    fun `returns guest defaults when no user`() = runTest {
        val userRepo = mockk<UserRepository>()
        coEvery { userRepo.getPrimaryUser() } returns null

        val useCase = GetAnalyticsOverviewDataUseCase(
            userRepository = userRepo,
            dailyLogRepository = mockk(),
            workoutRepository = mockk(),
            bodyMeasurementRepository = mockk(),
        )

        val result = useCase(rangeDays = 30, nowEpochMillis = 1735689600000, timezone = TimeZone.getTimeZone("UTC"))
        assertEquals(true, result.isGuest)
        assertEquals(0, result.totalSteps)
    }

    @Test
    fun `aggregates analytics for active user`() = runTest {
        val userRepo = mockk<UserRepository>()
        val dailyRepo = mockk<DailyLogRepository>()
        val workoutRepo = mockk<WorkoutRepository>()
        val bodyRepo = mockk<BodyMeasurementRepository>()

        val user = mockk<UserEntity> { every { id } returns "u1" }
        val log1 = mockk<DailyLogEntity> {
            every { steps } returns 5000
            every { activeCalories } returns 300
            every { workoutMinutes } returns 35
            every { logDateEpochDay } returns 20000L
        }
        val log2 = mockk<DailyLogEntity> {
            every { steps } returns 7000
            every { activeCalories } returns 420
            every { workoutMinutes } returns 40
            every { logDateEpochDay } returns 20001L
        }
        val workout = mockk<WorkoutEntity> { every { completedAtEpochMillis } returns 1735689600000 }
        val measurement1 = mockk<BodyMeasurementEntity> {
            every { recordedAtEpochMillis } returns 1L
            every { weightKg } returns 80f
        }
        val measurement2 = mockk<BodyMeasurementEntity> {
            every { recordedAtEpochMillis } returns 2L
            every { weightKg } returns 78.5f
        }

        coEvery { userRepo.getPrimaryUser() } returns user
        coEvery { dailyRepo.getLogsInRange("u1", any(), any()) } returns listOf(log1, log2)
        every { workoutRepo.observeWorkoutsByStatus("u1", "completed") } returns flowOf(listOf(workout))
        every { bodyRepo.observeMeasurementsInRange("u1", any(), any()) } returns flowOf(listOf(measurement1, measurement2))

        val result = GetAnalyticsOverviewDataUseCase(userRepo, dailyRepo, workoutRepo, bodyRepo)(
            rangeDays = 30,
            nowEpochMillis = 1735689600000,
            timezone = TimeZone.getTimeZone("UTC"),
        )

        assertEquals(false, result.isGuest)
        assertEquals(12000, result.totalSteps)
        assertEquals(720, result.totalCalories)
        assertEquals(75, result.totalWorkoutMinutes)
        assertEquals(1, result.completedWorkouts)
    }
}
