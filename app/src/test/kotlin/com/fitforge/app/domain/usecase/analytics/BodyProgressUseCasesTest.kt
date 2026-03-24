package com.fitforge.app.domain.usecase.analytics

import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import com.fitforge.app.domain.repository.BodyMeasurementRepository
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.testutil.CoreFixtures
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.util.TimeZone
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BodyProgressUseCasesTest {
    @Test
    fun `get body progress returns guest defaults when no user`() = runTest {
        val userRepo = mockk<UserRepository>()
        val bodyRepo = mockk<BodyMeasurementRepository>()
        coEvery { userRepo.getPrimaryUser() } returns null

        val result = GetBodyProgressDataUseCase(userRepo, bodyRepo)(
            rangeDays = 90,
            nowEpochMillis = 1735689600000,
            timezone = TimeZone.getTimeZone("UTC"),
        )

        assertEquals(true, result.isGuest)
        assertEquals(0, result.entries.size)
    }

    @Test
    fun `get body progress maps trends and latest values`() = runTest {
        val userRepo = mockk<UserRepository>()
        val bodyRepo = mockk<BodyMeasurementRepository>()
        coEvery { userRepo.getPrimaryUser() } returns CoreFixtures.user(id = "u1")

        val m1: BodyMeasurementEntity = CoreFixtures.measurement(id = "m1", userId = "u1", recordedAt = 1000).copy(weightKg = 80f, bodyFatPercent = 20f)
        val m2: BodyMeasurementEntity = CoreFixtures.measurement(id = "m2", userId = "u1", recordedAt = 2000).copy(weightKg = 79f, bodyFatPercent = 19f)
        every { bodyRepo.observeMeasurementsInRange("u1", any(), any()) } returns flowOf(listOf(m1, m2))

        val result = GetBodyProgressDataUseCase(userRepo, bodyRepo)(90, 1735689600000, TimeZone.getTimeZone("UTC"))

        assertEquals(false, result.isGuest)
        assertEquals(79f, result.latestWeightKg)
        assertEquals(2, result.weightTrend.size)
        assertEquals(1, result.photoUris.size)
    }

    @Test
    fun `add body measurement persists entity`() = runTest {
        val userRepo = mockk<UserRepository>()
        val bodyRepo = mockk<BodyMeasurementRepository>()
        coEvery { userRepo.getPrimaryUser() } returns CoreFixtures.user(id = "u1")
        coEvery { bodyRepo.upsertMeasurement(any()) } returns Unit

        AddBodyMeasurementUseCase(userRepo, bodyRepo)(
            weightKg = 78.6f,
            bodyFatPercent = 18.3f,
            photoUri = "content://photo1",
            notes = "progress",
            nowEpochMillis = 100L,
        )

        coVerify(exactly = 1) { bodyRepo.upsertMeasurement(any()) }
    }
}
