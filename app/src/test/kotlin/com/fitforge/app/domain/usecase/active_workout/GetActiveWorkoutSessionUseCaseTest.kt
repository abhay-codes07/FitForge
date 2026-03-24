package com.fitforge.app.domain.usecase.active_workout

import com.fitforge.app.data.local.db.entity.ExerciseEntity
import com.fitforge.app.data.local.db.entity.ExerciseSetEntity
import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity
import com.fitforge.app.domain.repository.ExerciseRepository
import com.fitforge.app.domain.repository.ExerciseSetRepository
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutExerciseRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class GetActiveWorkoutSessionUseCaseTest {
    @Test
    fun `loads session with current index based on completed sets`() = runTest {
        val userRepository = mockk<UserRepository>()
        val workoutRepository = mockk<WorkoutRepository>()
        val workoutExerciseRepository = mockk<WorkoutExerciseRepository>()
        val exerciseRepository = mockk<ExerciseRepository>()
        val exerciseSetRepository = mockk<ExerciseSetRepository>()

        val user = mockk<UserEntity> { every { id } returns "u1" }
        val workout = WorkoutEntity(
            id = "w1",
            userId = "u1",
            name = "Leg Day",
            description = "",
            workoutType = "strength",
            difficulty = "intermediate",
            estimatedDurationMinutes = 45,
            estimatedCalories = 320,
            source = "custom",
            scheduledDateEpochMillis = 2L,
            completedAtEpochMillis = null,
            status = "planned",
            notes = null,
            createdAtEpochMillis = 1L,
            updatedAtEpochMillis = 1L,
        )
        val firstExercise = WorkoutExerciseEntity(
            id = "we1",
            workoutId = "w1",
            exerciseId = "e1",
            sequenceIndex = 0,
            targetSets = 2,
            targetRepsMin = 8,
            targetRepsMax = 12,
            targetDurationSeconds = null,
            targetDistanceMeters = null,
            targetWeightKg = null,
            restDurationSeconds = 60,
            supersetGroup = null,
            notes = null,
            isOptional = false,
        )
        val secondExercise = firstExercise.copy(
            id = "we2",
            exerciseId = "e2",
            sequenceIndex = 1,
        )

        coEvery { userRepository.getPrimaryUser() } returns user
        every { workoutRepository.observeScheduledWorkoutsForRange("u1", any(), any()) } returns flowOf(listOf(workout))
        coEvery { workoutExerciseRepository.getExercisesForWorkout("w1") } returns listOf(firstExercise, secondExercise)
        coEvery { exerciseRepository.getExercise("e1") } returns testExercise("e1", "Squat")
        coEvery { exerciseRepository.getExercise("e2") } returns testExercise("e2", "Lunge")
        every { exerciseSetRepository.observeSetsForWorkout("w1") } returns flowOf(
            listOf(
                testCompletedSet(workoutId = "w1", workoutExerciseId = "we1", exerciseId = "e1", setNumber = 1),
                testCompletedSet(workoutId = "w1", workoutExerciseId = "we1", exerciseId = "e1", setNumber = 2),
            ),
        )

        val result = GetActiveWorkoutSessionUseCase(
            userRepository,
            workoutRepository,
            workoutExerciseRepository,
            exerciseRepository,
            exerciseSetRepository,
        ).invoke(nowEpochMillis = 1L)

        assertEquals("w1", result.workoutId)
        assertEquals(2, result.exercises.size)
        assertEquals(1, result.currentExerciseIndex)
    }

    @Test
    fun `throws when no workout available`() {
        val userRepository = mockk<UserRepository>()
        val workoutRepository = mockk<WorkoutRepository>()
        val workoutExerciseRepository = mockk<WorkoutExerciseRepository>()
        val exerciseRepository = mockk<ExerciseRepository>()
        val exerciseSetRepository = mockk<ExerciseSetRepository>()

        val user = mockk<UserEntity> { every { id } returns "u1" }

        coEvery { userRepository.getPrimaryUser() } returns user
        every { workoutRepository.observeScheduledWorkoutsForRange("u1", any(), any()) } returns flowOf(emptyList())
        every { workoutRepository.observeWorkoutsForUser("u1") } returns flowOf(emptyList())

        val useCase = GetActiveWorkoutSessionUseCase(
            userRepository,
            workoutRepository,
            workoutExerciseRepository,
            exerciseRepository,
            exerciseSetRepository,
        )

        assertThrows(IllegalStateException::class.java) {
            runTest { useCase.invoke(nowEpochMillis = 1L) }
        }
    }

    private fun testExercise(id: String, name: String): ExerciseEntity = ExerciseEntity(
        id = id,
        name = name,
        description = "",
        category = "Strength",
        difficulty = "Beginner",
        equipment = emptySet(),
        primaryMuscles = emptySet(),
        secondaryMuscles = emptySet(),
        instructions = emptyList(),
        estimatedDurationSeconds = 60,
        estimatedCalories = 10,
        imageUrl = null,
        videoUrl = null,
        isPremium = false,
        source = "db",
        createdAtEpochMillis = 1L,
        updatedAtEpochMillis = 1L,
    )

    private fun testCompletedSet(
        workoutId: String,
        workoutExerciseId: String,
        exerciseId: String,
        setNumber: Int,
    ): ExerciseSetEntity = ExerciseSetEntity(
        id = "$workoutExerciseId-$setNumber",
        workoutId = workoutId,
        workoutExerciseId = workoutExerciseId,
        exerciseId = exerciseId,
        setNumber = setNumber,
        reps = 10,
        weightKg = null,
        durationSeconds = null,
        distanceMeters = null,
        restDurationSeconds = 60,
        rpe = null,
        isWarmUp = false,
        completedAtEpochMillis = 10L,
        notes = null,
    )
}
