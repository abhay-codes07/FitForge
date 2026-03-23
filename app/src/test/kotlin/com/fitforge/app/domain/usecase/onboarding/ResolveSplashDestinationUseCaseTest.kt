package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.navigation.Screen
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ResolveSplashDestinationUseCaseTest {

    @Test
    fun `returns welcome route when onboarding is incomplete`() = runTest {
        val useCase = ResolveSplashDestinationUseCase(FakeOnboardingRepository(false))

        val route = useCase()

        assertEquals(Screen.Welcome.route, route)
    }

    @Test
    fun `returns home route when onboarding is complete`() = runTest {
        val useCase = ResolveSplashDestinationUseCase(FakeOnboardingRepository(true))

        val route = useCase()

        assertEquals(Screen.Home.route, route)
    }

    private class FakeOnboardingRepository(
        private val isComplete: Boolean,
    ) : OnboardingRepository {
        override suspend fun isOnboardingComplete(): Boolean = isComplete
        override suspend fun getSelectedGoals(): Set<String> = emptySet()
        override suspend fun setSelectedGoals(values: Set<String>) = Unit
        override suspend fun getPreferredUnitSystem(): String = "metric"
        override suspend fun setPreferredUnitSystem(value: String) = Unit
        override suspend fun getHeightValue(): Float? = null
        override suspend fun setHeightValue(value: Float) = Unit
        override suspend fun getWeightValue(): Float? = null
        override suspend fun setWeightValue(value: Float) = Unit
        override suspend fun getAgeValue(): Float? = null
        override suspend fun setAgeValue(value: Float) = Unit
        override suspend fun getGenderValue(): String = "unspecified"
        override suspend fun setGenderValue(value: String) = Unit
        override suspend fun getFitnessLevel(): String = "unspecified"
        override suspend fun setFitnessLevel(value: String) = Unit
    }
}
