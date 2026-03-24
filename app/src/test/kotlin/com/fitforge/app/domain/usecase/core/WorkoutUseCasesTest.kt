package com.fitforge.app.domain.usecase.core

import com.fitforge.app.data.local.db.entity.ExerciseEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity
import com.fitforge.app.domain.repository.ExerciseRepository
import com.fitforge.app.domain.repository.WorkoutExerciseRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import com.fitforge.app.domain.usecase.workouts.CreateCustomWorkoutRequest
import com.fitforge.app.domain.usecase.workouts.CreateCustomWorkoutUseCase
import com.fitforge.app.domain.usecase.workouts.ObserveExerciseByDifficultyUseCase
import com.fitforge.app.domain.usecase.workouts.ObserveExerciseDetailUseCase
import com.fitforge.app.domain.usecase.workouts.ObserveExercisesByCategoryUseCase
import com.fitforge.app.domain.usecase.workouts.ObserveWorkoutDetailUseCase
import com.fitforge.app.domain.usecase.workouts.ObserveWorkoutExercisesUseCase
import com.fitforge.app.domain.usecase.workouts.SearchExercisesUseCase
import com.fitforge.app.testutil.CoreFixtures
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WorkoutUseCasesTest {
    @Test
    fun `search exercises trims query`() = runTest {
        val repo = mockk<ExerciseRepository>()
        val expected = listOf(mockk<ExerciseEntity>())
        every { repo.searchExercises("bench") } returns flowOf(expected)

        val actual = SearchExercisesUseCase(repo)("  bench  ").first()
        assertEquals(expected, actual)
    }

    @Test
    fun `observe exercises by category delegates`() = runTest {
        val repo = mockk<ExerciseRepository>()
        val expected = listOf(mockk<ExerciseEntity>())
        every { repo.observeExercisesByCategory("strength") } returns flowOf(expected)

        val actual = ObserveExercisesByCategoryUseCase(repo)("strength").first()
        assertEquals(expected, actual)
    }

    @Test
    fun `observe exercise by difficulty delegates`() = runTest {
        val repo = mockk<ExerciseRepository>()
        val expected = listOf(mockk<ExerciseEntity>())
        every { repo.observeExercisesByDifficulty("advanced") } returns flowOf(expected)

        val actual = ObserveExerciseByDifficultyUseCase(repo)("advanced").first()
        assertEquals(expected, actual)
    }

    @Test
    fun `observe exercise detail delegates`() = runTest {
        val repo = mockk<ExerciseRepository>()
        val expected = mockk<ExerciseEntity>()
        every { repo.observeExercise("e1") } returns flowOf(expected)

        val actual = ObserveExerciseDetailUseCase(repo)("e1").first()
        assertEquals(expected, actual)
    }

    @Test
    fun `create custom workout validates and persists ordered exercises`() = runTest {
        val workoutRepo = mockk<WorkoutRepository>()
        val workoutExerciseRepo = mockk<WorkoutExerciseRepository>()
        coEvery { workoutRepo.upsertWorkout(any()) } returns Unit
        coEvery { workoutExerciseRepo.deleteExercisesByWorkoutId(any()) } returns Unit
        coEvery { workoutExerciseRepo.upsertWorkoutExercises(any()) } returns Unit

        val workout: WorkoutEntity = CoreFixtures.workout(id = "w1")
        val e1: WorkoutExerciseEntity = CoreFixtures.workoutExercise(id = "we1", workoutId = "w1", sequenceIndex = 2)
        val e2: WorkoutExerciseEntity = CoreFixtures.workoutExercise(id = "we2", workoutId = "w1", sequenceIndex = 1)

        CreateCustomWorkoutUseCase(workoutRepo, workoutExerciseRepo)(
            CreateCustomWorkoutRequest(workout = workout, workoutExercises = listOf(e1, e2)),
        )

        coVerify(exactly = 1) { workoutRepo.upsertWorkout(workout) }
        coVerify(exactly = 1) { workoutExerciseRepo.deleteExercisesByWorkoutId("w1") }
        coVerify(exactly = 1) {
            workoutExerciseRepo.upsertWorkoutExercises(match { list ->
                list.size == 2 && list[0].sequenceIndex == 1 && list[1].sequenceIndex == 2
            })
        }
    }

    @Test
    fun `create custom workout rejects empty list`() = runTest {
        val workoutRepo = mockk<WorkoutRepository>()
        val workoutExerciseRepo = mockk<WorkoutExerciseRepository>()
        val workout = CoreFixtures.workout(id = "w1")

        var thrown: Throwable? = null
        try {
            CreateCustomWorkoutUseCase(workoutRepo, workoutExerciseRepo)(
                CreateCustomWorkoutRequest(workout, emptyList()),
            )
        } catch (error: Throwable) {
            thrown = error
        }

        assertEquals(IllegalArgumentException::class, thrown!!::class)
    }

    @Test
    fun `observe workout detail delegates`() = runTest {
        val repo = mockk<WorkoutRepository>()
        val expected = mockk<WorkoutEntity>()
        every { repo.observeWorkout("w1") } returns flowOf(expected)

        val actual = ObserveWorkoutDetailUseCase(repo)("w1").first()
        assertEquals(expected, actual)
    }

    @Test
    fun `observe workout exercises delegates`() = runTest {
        val repo = mockk<WorkoutExerciseRepository>()
        val expected = listOf(mockk<WorkoutExerciseEntity>())
        every { repo.observeExercisesForWorkout("w1") } returns flowOf(expected)

        val actual = ObserveWorkoutExercisesUseCase(repo)("w1").first()
        assertEquals(expected, actual)
    }
}
