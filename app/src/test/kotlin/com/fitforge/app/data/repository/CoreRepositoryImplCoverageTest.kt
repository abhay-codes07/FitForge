package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.FitForgeDatabase
import com.fitforge.app.testutil.CoreFixtures
import com.fitforge.app.testutil.createInMemoryDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CoreRepositoryImplCoverageTest {
    private lateinit var db: FitForgeDatabase

    private lateinit var userRepository: UserRepositoryImpl
    private lateinit var exerciseRepository: ExerciseRepositoryImpl
    private lateinit var workoutRepository: WorkoutRepositoryImpl
    private lateinit var workoutExerciseRepository: WorkoutExerciseRepositoryImpl
    private lateinit var exerciseSetRepository: ExerciseSetRepositoryImpl
    private lateinit var bodyMeasurementRepository: BodyMeasurementRepositoryImpl
    private lateinit var dailyLogRepository: DailyLogRepositoryImpl
    private lateinit var achievementRepository: AchievementRepositoryImpl
    private lateinit var programRepository: ProgramRepositoryImpl
    private lateinit var gpsRepository: GpsRoutePointRepositoryImpl
    private lateinit var personalRecordRepository: PersonalRecordRepositoryImpl
    private lateinit var challengeRepository: ChallengeRepositoryImpl

    @Before
    fun setUp() = runBlocking {
        db = createInMemoryDatabase()

        userRepository = UserRepositoryImpl(db.userDao())
        exerciseRepository = ExerciseRepositoryImpl(db.exerciseDao())
        workoutRepository = WorkoutRepositoryImpl(db.workoutDao())
        workoutExerciseRepository = WorkoutExerciseRepositoryImpl(db.workoutExerciseDao())
        exerciseSetRepository = ExerciseSetRepositoryImpl(db.exerciseSetDao())
        bodyMeasurementRepository = BodyMeasurementRepositoryImpl(db.bodyMeasurementDao())
        dailyLogRepository = DailyLogRepositoryImpl(db.dailyLogDao())
        achievementRepository = AchievementRepositoryImpl(db.achievementDao())
        programRepository = ProgramRepositoryImpl(db.programDao())
        gpsRepository = GpsRoutePointRepositoryImpl(db.gpsRoutePointDao())
        personalRecordRepository = PersonalRecordRepositoryImpl(db.personalRecordDao())
        challengeRepository = ChallengeRepositoryImpl(db.challengeDao())

        userRepository.upsertUser(CoreFixtures.user())
        userRepository.upsertUser(CoreFixtures.user(CoreFixtures.USER_TWO_ID, createdAtOffset = 100))
        exerciseRepository.upsertExercise(CoreFixtures.exercise())
        workoutRepository.upsertWorkout(CoreFixtures.workout())
        workoutExerciseRepository.upsertWorkoutExercise(CoreFixtures.workoutExercise())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun coversRepositories() = runBlocking {
        assertNotNull(userRepository.getUser(CoreFixtures.USER_ID))
        assertEquals(CoreFixtures.USER_ID, userRepository.observePrimaryUser().first()?.id)

        assertEquals(1, exerciseRepository.observeExercisesByCategory("strength").first().size)
        assertEquals(1, exerciseRepository.searchExercises("Exercise").first().size)

        assertEquals(1, workoutRepository.observeWorkoutsForUser(CoreFixtures.USER_ID).first().size)
        assertEquals(CoreFixtures.WORKOUT_ID, workoutRepository.observeWorkout(CoreFixtures.WORKOUT_ID).first()?.id)

        assertEquals(1, workoutExerciseRepository.observeExercisesForWorkout(CoreFixtures.WORKOUT_ID).first().size)

        exerciseSetRepository.upsertExerciseSet(CoreFixtures.exerciseSet())
        assertEquals(1, exerciseSetRepository.observeSetsForWorkout(CoreFixtures.WORKOUT_ID).first().size)

        bodyMeasurementRepository.upsertMeasurement(CoreFixtures.measurement())
        assertNotNull(bodyMeasurementRepository.getLatestMeasurement(CoreFixtures.USER_ID))

        dailyLogRepository.upsertLog(CoreFixtures.dailyLog())
        assertNotNull(dailyLogRepository.getLogForDay(CoreFixtures.USER_ID, 1000L))

        achievementRepository.upsertAchievement(CoreFixtures.achievement())
        assertEquals(1, achievementRepository.observeUnlockedAchievements(CoreFixtures.USER_ID).first().size)

        programRepository.upsertProgram(CoreFixtures.program())
        assertEquals(1, programRepository.observeProgramsByGoal("fat_loss").first().size)

        gpsRepository.insertRoutePoint(CoreFixtures.gpsPoint())
        assertEquals(1, gpsRepository.getRoutePoints(CoreFixtures.WORKOUT_ID).size)

        personalRecordRepository.upsertRecord(CoreFixtures.personalRecord())
        assertNotNull(personalRecordRepository.observeTopRecordForMetric(CoreFixtures.USER_ID, "one_rep_max").first())

        challengeRepository.upsertChallenge(CoreFixtures.challenge())
        assertTrue(challengeRepository.observeChallengesForParticipant(CoreFixtures.USER_TWO_ID).first().isNotEmpty())
    }
}
