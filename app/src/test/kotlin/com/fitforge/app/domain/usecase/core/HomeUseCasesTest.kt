package com.fitforge.app.domain.usecase.core

import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import com.fitforge.app.data.local.db.entity.DailyLogEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.domain.repository.BodyMeasurementRepository
import com.fitforge.app.domain.repository.DailyLogRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import com.fitforge.app.domain.usecase.home.ObserveLatestBodyMeasurementUseCase
import com.fitforge.app.domain.usecase.home.ObserveNextScheduledWorkoutUseCase
import com.fitforge.app.domain.usecase.home.ObserveRecentCompletedWorkoutsUseCase
import com.fitforge.app.domain.usecase.home.ObserveTodayLogUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HomeUseCasesTest {
    @Test
    fun `observe today log delegates to repository`() = runTest {
        val repo = mockk<DailyLogRepository>()
        val expected: DailyLogEntity? = null
        every { repo.observeLogForDay("u1", 10L) } returns flowOf(expected)

        val actual = ObserveTodayLogUseCase(repo)("u1", 10L).first()
        assertEquals(expected, actual)
    }

    @Test
    fun `observe recent completed workouts delegates to repository`() = runTest {
        val repo = mockk<WorkoutRepository>()
        val workouts = listOf(mockk<WorkoutEntity>())
        every { repo.observeRecentCompletedWorkouts("u1", 3) } returns flowOf(workouts)

        val actual = ObserveRecentCompletedWorkoutsUseCase(repo)("u1", 3).first()
        assertEquals(workouts, actual)
    }

    @Test
    fun `observe next scheduled workout delegates to repository`() = runTest {
        val repo = mockk<WorkoutRepository>()
        val workout = mockk<WorkoutEntity>()
        every { repo.observeNextScheduledWorkout("u1", 1L, 2L) } returns flowOf(workout)

        val actual = ObserveNextScheduledWorkoutUseCase(repo)("u1", 1L, 2L).first()
        assertEquals(workout, actual)
    }

    @Test
    fun `observe latest body measurement delegates to repository`() = runTest {
        val repo = mockk<BodyMeasurementRepository>()
        val measurement = mockk<BodyMeasurementEntity>()
        every { repo.observeLatestMeasurement("u1") } returns flowOf(measurement)

        val actual = ObserveLatestBodyMeasurementUseCase(repo)("u1").first()
        assertEquals(measurement, actual)
    }
}
