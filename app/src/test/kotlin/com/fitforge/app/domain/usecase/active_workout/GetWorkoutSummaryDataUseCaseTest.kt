package com.fitforge.app.domain.usecase.active_workout

import com.fitforge.app.data.local.db.entity.ExerciseSetEntity
import com.fitforge.app.data.local.db.entity.PersonalRecordEntity
import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.domain.repository.ExerciseSetRepository
import com.fitforge.app.domain.repository.PersonalRecordRepository
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class GetWorkoutSummaryDataUseCaseTest {
    @Test
    fun `returns summary from latest completed workout`() = runTest {
        val userRepository = mockk<UserRepository>()
        val workoutRepository = mockk<WorkoutRepository>()
        val exerciseSetRepository = mockk<ExerciseSetRepository>()
        val personalRecordRepository = mockk<PersonalRecordRepository>()

        val user = mockk<UserEntity> { every { id } returns "u1" }
        val workout = WorkoutEntity(
            id = "w1",
            userId = "u1",
            name = "Push Day",
            description = "",
            workoutType = "strength",
            difficulty = "intermediate",
            estimatedDurationMinutes = 50,
            estimatedCalories = 420,
            source = "custom",
            scheduledDateEpochMillis = null,
            completedAtEpochMillis = 1000L,
            status = "completed",
            notes = null,
            createdAtEpochMillis = 1L,
            updatedAtEpochMillis = 1L,
        )

        coEvery { userRepository.getPrimaryUser() } returns user
        every { workoutRepository.observeRecentCompletedWorkouts("u1", 1) } returns flowOf(listOf(workout))
        every { exerciseSetRepository.observeSetsForWorkout("w1") } returns flowOf(
            listOf(
                set("w1", "we1", reps = 10, weight = 20f),
                set("w1", "we1", reps = 12, weight = 22.5f),
            ),
        )
        every { personalRecordRepository.observeRecordsInRange("u1", any(), any()) } returns flowOf(
            listOf(
                pr("u1", "w1", "Bench", 100f),
            ),
        )

        val result = GetWorkoutSummaryDataUseCase(
            userRepository,
            workoutRepository,
            exerciseSetRepository,
            personalRecordRepository,
        ).invoke(nowEpochMillis = 2000L)

        assertEquals("Push Day", result.workoutName)
        assertEquals(2, result.totalSets)
        assertEquals(22, result.totalReps)
        assertEquals(470f, result.totalVolumeKg)
        assertEquals(1, result.personalRecordCount)
    }

    @Test
    fun `throws when no completed workouts`() {
        val userRepository = mockk<UserRepository>()
        val workoutRepository = mockk<WorkoutRepository>()
        val exerciseSetRepository = mockk<ExerciseSetRepository>()
        val personalRecordRepository = mockk<PersonalRecordRepository>()

        val user = mockk<UserEntity> { every { id } returns "u1" }
        coEvery { userRepository.getPrimaryUser() } returns user
        every { workoutRepository.observeRecentCompletedWorkouts("u1", 1) } returns flowOf(emptyList())

        val useCase = GetWorkoutSummaryDataUseCase(
            userRepository,
            workoutRepository,
            exerciseSetRepository,
            personalRecordRepository,
        )

        assertThrows(IllegalStateException::class.java) {
            runTest { useCase.invoke() }
        }
    }

    private fun set(workoutId: String, workoutExerciseId: String, reps: Int, weight: Float): ExerciseSetEntity {
        return ExerciseSetEntity(
            id = "$workoutExerciseId-$reps",
            workoutId = workoutId,
            workoutExerciseId = workoutExerciseId,
            exerciseId = "e1",
            setNumber = 1,
            reps = reps,
            weightKg = weight,
            durationSeconds = null,
            distanceMeters = null,
            restDurationSeconds = 60,
            rpe = null,
            isWarmUp = false,
            completedAtEpochMillis = 1000L,
            notes = null,
        )
    }

    private fun pr(userId: String, workoutId: String, exerciseName: String, value: Float): PersonalRecordEntity {
        return PersonalRecordEntity(
            id = "$workoutId-$exerciseName",
            userId = userId,
            exerciseId = "e1",
            workoutId = workoutId,
            metricType = "weight",
            value = value.toDouble(),
            unit = "kg",
            achievedAtEpochMillis = 1000L,
            notes = null,
        )
    }
}
