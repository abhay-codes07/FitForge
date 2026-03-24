package com.fitforge.app.domain.usecase.core

import com.fitforge.app.data.local.db.entity.ProgramEntity
import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.domain.repository.ProgramRepository
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.usecase.profile.ObservePrimaryUserProfileUseCase
import com.fitforge.app.domain.usecase.profile.ObserveProgramsByGoalUseCase
import com.fitforge.app.domain.usecase.profile.ObserveProgramsByPremiumStateUseCase
import com.fitforge.app.domain.usecase.profile.ObserveUserProfileUseCase
import com.fitforge.app.domain.usecase.profile.UpdateUserProfileUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.Test

class ProfileUseCasesTest {
    @Test
    fun `observe user profile delegates`() = runTest {
        val repo = mockk<UserRepository>()
        val expected = mockk<UserEntity>()
        every { repo.observeUser("u1") } returns flowOf(expected)

        val actual = ObserveUserProfileUseCase(repo)("u1").first()
        assertEquals(expected, actual)
    }

    @Test
    fun `observe primary user profile delegates`() = runTest {
        val repo = mockk<UserRepository>()
        val expected = mockk<UserEntity>()
        every { repo.observePrimaryUser() } returns flowOf(expected)

        val actual = ObservePrimaryUserProfileUseCase(repo)().first()
        assertEquals(expected, actual)
    }

    @Test
    fun `update user profile validates and persists`() = runTest {
        val repo = mockk<UserRepository>()
        val user = mockk<UserEntity> {
            every { id } returns "u1"
            every { displayName } returns "Abhay"
        }
        coEvery { repo.upsertUser(any()) } returns Unit

        UpdateUserProfileUseCase(repo)(user)

        coVerify(exactly = 1) { repo.upsertUser(user) }
    }

    @Test
    fun `update user profile rejects blank name`() {
        val repo = mockk<UserRepository>()
        val user = mockk<UserEntity> {
            every { id } returns "u1"
            every { displayName } returns "  "
        }

        var thrown: Throwable? = null
        try {
            runTest { UpdateUserProfileUseCase(repo)(user) }
        } catch (error: Throwable) {
            thrown = error
        }

        assertEquals(IllegalArgumentException::class, thrown!!::class)
    }

    @Test
    fun `observe programs by goal and premium delegates`() = runTest {
        val repo = mockk<ProgramRepository>()
        val goalPrograms = listOf(mockk<ProgramEntity>())
        val premiumPrograms = listOf(mockk<ProgramEntity>())
        every { repo.observeProgramsByGoal("fat_loss") } returns flowOf(goalPrograms)
        every { repo.observeProgramsByPremiumState(true) } returns flowOf(premiumPrograms)

        assertEquals(goalPrograms, ObserveProgramsByGoalUseCase(repo)("fat_loss").first())
        assertEquals(premiumPrograms, ObserveProgramsByPremiumStateUseCase(repo)(true).first())
    }
}

