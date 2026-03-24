package com.fitforge.app.domain.usecase.core

import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import com.fitforge.app.data.local.db.entity.DailyLogEntity
import com.fitforge.app.data.local.db.entity.PersonalRecordEntity
import com.fitforge.app.domain.repository.BodyMeasurementRepository
import com.fitforge.app.domain.repository.DailyLogRepository
import com.fitforge.app.domain.repository.PersonalRecordRepository
import com.fitforge.app.domain.usecase.analytics.ObserveBodyMeasurementsInRangeUseCase
import com.fitforge.app.domain.usecase.analytics.ObserveDailyLogsInRangeUseCase
import com.fitforge.app.domain.usecase.analytics.ObservePersonalRecordsInRangeUseCase
import com.fitforge.app.domain.usecase.analytics.ObserveTopPersonalRecordForMetricUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AnalyticsUseCasesTest {
    @Test
    fun `observe daily logs in range delegates`() = runTest {
        val repo = mockk<DailyLogRepository>()
        val expected = listOf(mockk<DailyLogEntity>())
        every { repo.observeLogsInRange("u1", 1L, 2L) } returns flowOf(expected)

        val actual = ObserveDailyLogsInRangeUseCase(repo)("u1", 1L, 2L).first()
        assertEquals(expected, actual)
    }

    @Test
    fun `observe body measurements in range delegates`() = runTest {
        val repo = mockk<BodyMeasurementRepository>()
        val expected = listOf(mockk<BodyMeasurementEntity>())
        every { repo.observeMeasurementsInRange("u1", 1L, 2L) } returns flowOf(expected)

        val actual = ObserveBodyMeasurementsInRangeUseCase(repo)("u1", 1L, 2L).first()
        assertEquals(expected, actual)
    }

    @Test
    fun `observe personal records in range delegates`() = runTest {
        val repo = mockk<PersonalRecordRepository>()
        val expected = listOf(mockk<PersonalRecordEntity>())
        every { repo.observeRecordsInRange("u1", 1L, 2L) } returns flowOf(expected)

        val actual = ObservePersonalRecordsInRangeUseCase(repo)("u1", 1L, 2L).first()
        assertEquals(expected, actual)
    }

    @Test
    fun `observe top personal record delegates`() = runTest {
        val repo = mockk<PersonalRecordRepository>()
        val expected = mockk<PersonalRecordEntity>()
        every { repo.observeTopRecordForMetric("u1", "one_rep_max") } returns flowOf(expected)

        val actual = ObserveTopPersonalRecordForMetricUseCase(repo)("u1", "one_rep_max").first()
        assertEquals(expected, actual)
    }
}
