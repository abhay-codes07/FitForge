package com.fitforge.app.domain.usecase.sync

import com.fitforge.app.data.local.db.entity.ExerciseSetEntity
import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity
import com.fitforge.app.domain.repository.CloudWorkoutSyncRepository
import com.fitforge.app.domain.repository.ExerciseSetRepository
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutExerciseRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WorkoutCloudSyncUseCasesTest {

    @Test
    fun `push workout forwards complete workout data to cloud repository`() = runTest {
        val cloudRepository = FakeCloudWorkoutSyncRepository()
        val workoutExerciseRepository = FakeWorkoutExerciseRepository(
            exercises = listOf(sampleWorkoutExercise(id = "we1", workoutId = "w_push")),
        )
        val exerciseSetRepository = FakeExerciseSetRepository(
            sets = listOf(sampleExerciseSet(id = "set1", workoutId = "w_push")),
        )
        val workout = sampleWorkout(id = "w_push")

        val result = PushWorkoutToCloudUseCase(
            cloudRepository,
            workoutExerciseRepository,
            exerciseSetRepository,
        )(workout)

        assertEquals(true, result.isSuccess)
        assertEquals("w_push", cloudRepository.lastPushedWorkout?.id)
        assertEquals(1, cloudRepository.lastPushedWorkoutExercises.size)
        assertEquals("we1", cloudRepository.lastPushedWorkoutExercises.first().id)
        assertEquals(1, cloudRepository.lastPushedExerciseSets.size)
        assertEquals("set1", cloudRepository.lastPushedExerciseSets.first().id)
    }

    @Test
    fun `pull workouts stores remote workouts with exercises and sets locally`() = runTest {
        val remoteWorkouts = listOf(sampleWorkout(id = "w_remote"))
        val remoteExercises = listOf(sampleWorkoutExercise(id = "we_remote", workoutId = "w_remote"))
        val remoteSets = listOf(sampleExerciseSet(id = "set_remote", workoutId = "w_remote"))

        val cloudRepository = FakeCloudWorkoutSyncRepository(
            remoteWorkouts = remoteWorkouts,
            remoteWorkoutExercises = mapOf("w_remote" to remoteExercises),
            remoteExerciseSets = mapOf("w_remote" to remoteSets),
        )
        val userRepository = FakeUserRepository(primaryUser = sampleUser(id = "u1"))
        val workoutRepository = FakeWorkoutRepository()
        val workoutExerciseRepository = FakeWorkoutExerciseRepository()
        val exerciseSetRepository = FakeExerciseSetRepository()

        val syncedCount = PullWorkoutsFromCloudUseCase(
            userRepository = userRepository,
            workoutRepository = workoutRepository,
            workoutExerciseRepository = workoutExerciseRepository,
            exerciseSetRepository = exerciseSetRepository,
            cloudWorkoutSyncRepository = cloudRepository,
        )().getOrThrow()

        assertEquals(1, syncedCount)
        assertEquals(1, workoutRepository.lastUpsertedWorkouts.size)
        assertEquals("w_remote", workoutRepository.lastUpsertedWorkouts.first().id)
        assertEquals(1, workoutExerciseRepository.lastUpsertedWorkoutExercises.size)
        assertEquals("we_remote", workoutExerciseRepository.lastUpsertedWorkoutExercises.first().id)
        assertEquals(1, exerciseSetRepository.lastUpsertedExerciseSets.size)
        assertEquals("set_remote", exerciseSetRepository.lastUpsertedExerciseSets.first().id)
    }

    private class FakeCloudWorkoutSyncRepository(
        private val remoteWorkouts: List<WorkoutEntity> = emptyList(),
        private val remoteWorkoutExercises: Map<String, List<WorkoutExerciseEntity>> = emptyMap(),
        private val remoteExerciseSets: Map<String, List<ExerciseSetEntity>> = emptyMap(),
    ) : CloudWorkoutSyncRepository {
        var lastPushedWorkout: WorkoutEntity? = null
        var lastPushedWorkoutExercises: List<WorkoutExerciseEntity> = emptyList()
        var lastPushedExerciseSets: List<ExerciseSetEntity> = emptyList()

        override suspend fun pushWorkout(workout: WorkoutEntity): Result<Unit> {
            lastPushedWorkout = workout
            return Result.success(Unit)
        }

        override suspend fun pushWorkoutExercises(workoutExercises: List<WorkoutExerciseEntity>): Result<Unit> {
            lastPushedWorkoutExercises = workoutExercises
            return Result.success(Unit)
        }

        override suspend fun pushExerciseSets(exerciseSets: List<ExerciseSetEntity>): Result<Unit> {
            lastPushedExerciseSets = exerciseSets
            return Result.success(Unit)
        }

        override suspend fun pullWorkoutsForUser(userId: String): Result<List<WorkoutEntity>> {
            return Result.success(remoteWorkouts)
        }

        override suspend fun pullWorkoutExercisesForWorkout(workoutId: String): Result<List<WorkoutExerciseEntity>> {
            return Result.success(remoteWorkoutExercises[workoutId] ?: emptyList())
        }

        override suspend fun pullExerciseSetsForWorkout(workoutId: String): Result<List<ExerciseSetEntity>> {
            return Result.success(remoteExerciseSets[workoutId] ?: emptyList())
        }
    }

    private class FakeUserRepository(
        private val primaryUser: UserEntity?,
    ) : UserRepository {
        override fun observeUser(userId: String): Flow<UserEntity?> = emptyFlow()
        override suspend fun getUser(userId: String): UserEntity? = null
        override fun observePrimaryUser(): Flow<UserEntity?> = emptyFlow()
        override suspend fun getPrimaryUser(): UserEntity? = primaryUser
        override suspend fun upsertUser(user: UserEntity) = Unit
        override suspend fun upsertUsers(users: List<UserEntity>) = Unit
        override suspend fun deleteUser(user: UserEntity) = Unit
    }

    private class FakeWorkoutRepository : WorkoutRepository {
        var lastUpsertedWorkouts: List<WorkoutEntity> = emptyList()

        override fun observeWorkout(workoutId: String): Flow<WorkoutEntity?> = emptyFlow()
        override suspend fun getWorkout(workoutId: String): WorkoutEntity? = null
        override fun observeWorkoutsForUser(userId: String): Flow<List<WorkoutEntity>> = emptyFlow()
        override fun observeScheduledWorkoutsForRange(
            userId: String,
            startEpochMillis: Long,
            endEpochMillis: Long,
        ): Flow<List<WorkoutEntity>> = emptyFlow()

        override fun observeNextScheduledWorkout(
            userId: String,
            startEpochMillis: Long,
            endEpochMillis: Long,
        ): Flow<WorkoutEntity?> = emptyFlow()

        override fun observeWorkoutsByStatus(userId: String, status: String): Flow<List<WorkoutEntity>> = emptyFlow()
        override fun observeRecentCompletedWorkouts(userId: String, limit: Int): Flow<List<WorkoutEntity>> = emptyFlow()
        override suspend fun upsertWorkout(workout: WorkoutEntity) = Unit

        override suspend fun upsertWorkouts(workouts: List<WorkoutEntity>) {
            lastUpsertedWorkouts = workouts
        }

        override suspend fun deleteWorkout(workout: WorkoutEntity) = Unit
    }

    private class FakeWorkoutExerciseRepository(
        private val exercises: List<WorkoutExerciseEntity> = emptyList(),
    ) : WorkoutExerciseRepository {
        var lastUpsertedWorkoutExercises: List<WorkoutExerciseEntity> = emptyList()

        override fun observeWorkoutExercise(workoutExerciseId: String): Flow<WorkoutExerciseEntity?> = emptyFlow()
        override fun observeExercisesForWorkout(workoutId: String): Flow<List<WorkoutExerciseEntity>> = emptyFlow()
        override suspend fun getExercisesForWorkout(workoutId: String): List<WorkoutExerciseEntity> = exercises
        override suspend fun upsertWorkoutExercise(workoutExercise: WorkoutExerciseEntity) = Unit

        override suspend fun upsertWorkoutExercises(workoutExercises: List<WorkoutExerciseEntity>) {
            lastUpsertedWorkoutExercises = workoutExercises
        }

        override suspend fun deleteExercisesByWorkoutId(workoutId: String) = Unit
        override suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExerciseEntity) = Unit
    }

    private class FakeExerciseSetRepository(
        private val sets: List<ExerciseSetEntity> = emptyList(),
    ) : ExerciseSetRepository {
        var lastUpsertedExerciseSets: List<ExerciseSetEntity> = emptyList()

        override fun observeSetsForWorkout(workoutId: String): Flow<List<ExerciseSetEntity>> = emptyFlow()
        override fun observeSetsForWorkoutExercise(workoutExerciseId: String): Flow<List<ExerciseSetEntity>> = emptyFlow()
        override suspend fun getSetsForWorkout(workoutId: String): List<ExerciseSetEntity> = sets
        override suspend fun getSetsForWorkoutExercise(workoutExerciseId: String): List<ExerciseSetEntity> = emptyList()
        override suspend fun upsertExerciseSet(exerciseSet: ExerciseSetEntity) = Unit

        override suspend fun upsertExerciseSets(exerciseSets: List<ExerciseSetEntity>) {
            lastUpsertedExerciseSets = exerciseSets
        }

        override suspend fun deleteSetsByWorkoutExerciseId(workoutExerciseId: String) = Unit
        override suspend fun deleteExerciseSet(exerciseSet: ExerciseSetEntity) = Unit
    }
}

private fun sampleWorkout(id: String) = WorkoutEntity(
    id = id,
    userId = "u1",
    name = "Workout",
    description = "Desc",
    workoutType = "strength",
    difficulty = "beginner",
    estimatedDurationMinutes = 30,
    estimatedCalories = 250,
    source = "custom",
    scheduledDateEpochMillis = null,
    completedAtEpochMillis = null,
    status = "planned",
    notes = null,
    createdAtEpochMillis = 1L,
    updatedAtEpochMillis = 1L,
)

private fun sampleUser(id: String) = UserEntity(
    id = id,
    displayName = "Abhay",
    email = "abhay@example.com",
    photoUrl = null,
    birthDateEpochMillis = null,
    gender = "unspecified",
    heightCm = null,
    currentWeightKg = null,
    targetWeightKg = null,
    fitnessLevel = "beginner",
    primaryGoals = emptySet(),
    workoutLocations = emptySet(),
    availableEquipment = emptySet(),
    preferredUnitSystem = "metric",
    isNotificationEnabled = true,
    isHealthConnectLinked = false,
    isPremium = false,
    createdAtEpochMillis = 1L,
    updatedAtEpochMillis = 1L,
)

private fun sampleWorkoutExercise(id: String, workoutId: String) = WorkoutExerciseEntity(
    id = id,
    workoutId = workoutId,
    exerciseId = "ex1",
    sequenceIndex = 0,
    targetSets = 3,
    targetRepsMin = 8,
    targetRepsMax = 12,
    targetDurationSeconds = null,
    targetDistanceMeters = null,
    targetWeightKg = 50f,
    restDurationSeconds = 90,
    supersetGroup = null,
    notes = null,
    isOptional = false,
)

private fun sampleExerciseSet(id: String, workoutId: String) = ExerciseSetEntity(
    id = id,
    workoutId = workoutId,
    workoutExerciseId = "we1",
    exerciseId = "ex1",
    setNumber = 1,
    reps = 10,
    weightKg = 50f,
    durationSeconds = null,
    distanceMeters = null,
    restDurationSeconds = 90,
    rpe = 7.5f,
    isWarmUp = false,
    completedAtEpochMillis = 1000L,
    notes = null,
)
