package com.fitforge.app.data.local.db

import com.fitforge.app.testutil.CoreFixtures
import com.fitforge.app.testutil.createInMemoryDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CoreDaoCoverageTest {
    private lateinit var database: FitForgeDatabase

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun coversUserDaoQueries() = runBlocking {
        val dao = database.userDao()
        val user1 = CoreFixtures.user(CoreFixtures.USER_ID, createdAtOffset = 100)
        val user2 = CoreFixtures.user(CoreFixtures.USER_TWO_ID, createdAtOffset = 200)

        dao.upsert(user1)
        dao.upsertAll(listOf(user2))

        assertEquals(CoreFixtures.USER_ID, dao.getUser(CoreFixtures.USER_ID)?.id)
        assertEquals(CoreFixtures.USER_ID, dao.observeUser(CoreFixtures.USER_ID).first()?.id)
        assertEquals(CoreFixtures.USER_ID, dao.getPrimaryUser()?.id)
        assertEquals(CoreFixtures.USER_ID, dao.observePrimaryUser().first()?.id)

        dao.delete(user1)
        assertNull(dao.getUser(CoreFixtures.USER_ID))
    }

    @Test
    fun coversExerciseDaoQueries() = runBlocking {
        val dao = database.exerciseDao()
        val e1 = CoreFixtures.exercise(CoreFixtures.EXERCISE_ID, category = "strength", difficulty = "intermediate", isPremium = false)
        val e2 = CoreFixtures.exercise(CoreFixtures.EXERCISE_TWO_ID, category = "cardio", difficulty = "beginner", isPremium = true)

        dao.upsert(e1)
        dao.upsertAll(listOf(e2))

        assertEquals(2, dao.observeExercises().first().size)
        assertEquals(CoreFixtures.EXERCISE_ID, dao.getExercise(CoreFixtures.EXERCISE_ID)?.id)
        assertEquals(CoreFixtures.EXERCISE_ID, dao.observeExercise(CoreFixtures.EXERCISE_ID).first()?.id)
        assertEquals(1, dao.searchExercises("Exercise exercise_1").first().size)
        assertEquals(1, dao.observeExercisesByCategory("cardio").first().size)
        assertEquals(1, dao.observeExercisesByDifficulty("beginner").first().size)
        assertEquals(1, dao.observeExercisesByPremiumState(true).first().size)

        dao.delete(e1)
        assertNull(dao.getExercise(CoreFixtures.EXERCISE_ID))
    }

    @Test
    fun coversWorkoutAndWorkoutExerciseDaoQueries() = runBlocking {
        database.userDao().upsert(CoreFixtures.user())
        database.exerciseDao().upsert(CoreFixtures.exercise())

        val workoutDao = database.workoutDao()
        val workoutExerciseDao = database.workoutExerciseDao()

        val scheduled = CoreFixtures.workout(
            id = CoreFixtures.WORKOUT_ID,
            status = "scheduled",
            completedAt = null,
            scheduledDate = 1735689700000,
        )
        val completed = CoreFixtures.workout(
            id = CoreFixtures.WORKOUT_TWO_ID,
            status = "completed",
            completedAt = 1735689800000,
            scheduledDate = 1735689750000,
        )

        workoutDao.upsert(scheduled)
        workoutDao.upsertAll(listOf(completed))

        assertEquals(CoreFixtures.WORKOUT_ID, workoutDao.getWorkout(CoreFixtures.WORKOUT_ID)?.id)
        assertEquals(CoreFixtures.WORKOUT_ID, workoutDao.observeWorkout(CoreFixtures.WORKOUT_ID).first()?.id)
        assertEquals(2, workoutDao.observeWorkoutsForUser(CoreFixtures.USER_ID).first().size)
        assertEquals(2, workoutDao.observeScheduledWorkoutsForRange(CoreFixtures.USER_ID, 1735689600000, 1735689900000).first().size)
        assertEquals(CoreFixtures.WORKOUT_ID, workoutDao.observeNextScheduledWorkout(CoreFixtures.USER_ID, 1735689600000, 1735689900000).first()?.id)
        assertEquals(1, workoutDao.observeWorkoutsByStatus(CoreFixtures.USER_ID, "completed").first().size)
        assertEquals(1, workoutDao.observeRecentCompletedWorkouts(CoreFixtures.USER_ID, 1).first().size)

        val we1 = CoreFixtures.workoutExercise(id = CoreFixtures.WORKOUT_EXERCISE_ID, sequenceIndex = 0)
        val we2 = CoreFixtures.workoutExercise(id = "workout_exercise_2", sequenceIndex = 1)
        workoutExerciseDao.upsert(we1)
        workoutExerciseDao.upsertAll(listOf(we2))

        assertEquals(CoreFixtures.WORKOUT_EXERCISE_ID, workoutExerciseDao.observeWorkoutExercise(CoreFixtures.WORKOUT_EXERCISE_ID).first()?.id)
        assertEquals(2, workoutExerciseDao.observeExercisesForWorkout(CoreFixtures.WORKOUT_ID).first().size)
        assertEquals(2, workoutExerciseDao.getExercisesForWorkout(CoreFixtures.WORKOUT_ID).size)

        workoutExerciseDao.delete(we1)
        assertEquals(1, workoutExerciseDao.getExercisesForWorkout(CoreFixtures.WORKOUT_ID).size)

        workoutExerciseDao.deleteByWorkoutId(CoreFixtures.WORKOUT_ID)
        assertTrue(workoutExerciseDao.getExercisesForWorkout(CoreFixtures.WORKOUT_ID).isEmpty())

        workoutDao.delete(scheduled)
        assertNull(workoutDao.getWorkout(CoreFixtures.WORKOUT_ID))
    }

    @Test
    fun coversRemainingDaoQueries() = runBlocking {
        val userDao = database.userDao()
        val exerciseDao = database.exerciseDao()
        val workoutDao = database.workoutDao()
        val workoutExerciseDao = database.workoutExerciseDao()

        userDao.upsert(CoreFixtures.user())
        userDao.upsert(CoreFixtures.user(CoreFixtures.USER_TWO_ID, createdAtOffset = 200))
        exerciseDao.upsert(CoreFixtures.exercise())
        workoutDao.upsert(CoreFixtures.workout())
        workoutExerciseDao.upsert(CoreFixtures.workoutExercise())

        val setDao = database.exerciseSetDao()
        val set1 = CoreFixtures.exerciseSet(id = "set_1", setNumber = 1)
        val set2 = CoreFixtures.exerciseSet(id = "set_2", setNumber = 2)
        setDao.upsert(set1)
        setDao.upsertAll(listOf(set2))
        assertEquals(2, setDao.observeSetsForWorkout(CoreFixtures.WORKOUT_ID).first().size)
        assertEquals(2, setDao.observeSetsForWorkoutExercise(CoreFixtures.WORKOUT_EXERCISE_ID).first().size)
        assertEquals(2, setDao.getSetsForWorkoutExercise(CoreFixtures.WORKOUT_EXERCISE_ID).size)
        setDao.delete(set1)
        assertEquals(1, setDao.getSetsForWorkoutExercise(CoreFixtures.WORKOUT_EXERCISE_ID).size)
        setDao.deleteByWorkoutExerciseId(CoreFixtures.WORKOUT_EXERCISE_ID)
        assertTrue(setDao.getSetsForWorkoutExercise(CoreFixtures.WORKOUT_EXERCISE_ID).isEmpty())

        val bodyDao = database.bodyMeasurementDao()
        val m1 = CoreFixtures.measurement(id = "m1", recordedAt = 1735689600000)
        val m2 = CoreFixtures.measurement(id = "m2", recordedAt = 1735689700000)
        bodyDao.upsert(m1)
        bodyDao.upsertAll(listOf(m2))
        assertEquals(2, bodyDao.observeMeasurementsForUser(CoreFixtures.USER_ID).first().size)
        assertEquals("m2", bodyDao.observeLatestMeasurement(CoreFixtures.USER_ID).first()?.id)
        assertEquals("m2", bodyDao.getLatestMeasurement(CoreFixtures.USER_ID)?.id)
        assertEquals(2, bodyDao.observeMeasurementsInRange(CoreFixtures.USER_ID, 1735689500000, 1735689800000).first().size)
        bodyDao.delete(m1)
        assertEquals(1, bodyDao.observeMeasurementsForUser(CoreFixtures.USER_ID).first().size)

        val dailyDao = database.dailyLogDao()
        val d1 = CoreFixtures.dailyLog(id = "d1", epochDay = 100)
        val d2 = CoreFixtures.dailyLog(id = "d2", epochDay = 101)
        dailyDao.upsert(d1)
        dailyDao.upsertAll(listOf(d2))
        assertEquals("d1", dailyDao.getLogForDay(CoreFixtures.USER_ID, 100)?.id)
        assertEquals("d1", dailyDao.observeLogForDay(CoreFixtures.USER_ID, 100).first()?.id)
        assertEquals(2, dailyDao.observeLogsInRange(CoreFixtures.USER_ID, 100, 101).first().size)
        assertEquals(2, dailyDao.getLogsInRange(CoreFixtures.USER_ID, 100, 101).size)
        assertEquals("d2", dailyDao.observeLatestLog(CoreFixtures.USER_ID).first()?.id)
        dailyDao.delete(d1)
        assertNull(dailyDao.getLogForDay(CoreFixtures.USER_ID, 100))

        val achievementDao = database.achievementDao()
        val a1 = CoreFixtures.achievement(id = "a1", unlockedAt = 1735689700000)
        val a2 = CoreFixtures.achievement(id = "a2", unlockedAt = null)
        achievementDao.upsert(a1)
        achievementDao.upsertAll(listOf(a2))
        assertEquals(2, achievementDao.observeAchievementsForUser(CoreFixtures.USER_ID).first().size)
        assertEquals(1, achievementDao.observeUnlockedAchievements(CoreFixtures.USER_ID).first().size)
        assertEquals(2, achievementDao.observeAchievementsByCategory(CoreFixtures.USER_ID, "streak").first().size)
        achievementDao.delete(a1)
        assertEquals(1, achievementDao.observeAchievementsForUser(CoreFixtures.USER_ID).first().size)

        val programDao = database.programDao()
        val p1 = CoreFixtures.program(id = "p1", goal = "fat_loss", premium = false)
        val p2 = CoreFixtures.program(id = "p2", goal = "muscle_gain", premium = true)
        programDao.upsert(p1)
        programDao.upsertAll(listOf(p2))
        assertEquals(2, programDao.observePrograms().first().size)
        assertEquals("p1", programDao.observeProgram("p1").first()?.id)
        assertEquals(1, programDao.observeProgramsByGoal("fat_loss").first().size)
        assertEquals(1, programDao.observeProgramsByPremiumState(true).first().size)
        programDao.delete(p1)
        assertNull(programDao.observeProgram("p1").first())

        val gpsDao = database.gpsRoutePointDao()
        val g1Id = gpsDao.insert(CoreFixtures.gpsPoint(id = 0, ts = 1735689600000))
        val insertedIds = gpsDao.insertAll(listOf(CoreFixtures.gpsPoint(id = 0, ts = 1735689700000)))
        assertTrue(g1Id > 0)
        assertEquals(1, insertedIds.size)
        val points = gpsDao.observeRoutePoints(CoreFixtures.WORKOUT_ID).first()
        assertEquals(2, points.size)
        assertEquals(2, gpsDao.getRoutePoints(CoreFixtures.WORKOUT_ID).size)
        gpsDao.delete(points.first())
        assertEquals(1, gpsDao.getRoutePoints(CoreFixtures.WORKOUT_ID).size)
        gpsDao.deleteByWorkoutId(CoreFixtures.WORKOUT_ID)
        assertTrue(gpsDao.getRoutePoints(CoreFixtures.WORKOUT_ID).isEmpty())

        val prDao = database.personalRecordDao()
        val pr1 = CoreFixtures.personalRecord(id = "pr1")
        val pr2 = CoreFixtures.personalRecord(id = "pr2").copy(value = 120.0, achievedAtEpochMillis = 1735689800000)
        prDao.upsert(pr1)
        prDao.upsertAll(listOf(pr2))
        assertEquals(2, prDao.observeRecordsForUser(CoreFixtures.USER_ID).first().size)
        assertEquals(2, prDao.observeRecordsForExercise(CoreFixtures.USER_ID, CoreFixtures.EXERCISE_ID).first().size)
        assertEquals(120.0, prDao.observeTopRecordForMetric(CoreFixtures.USER_ID, "one_rep_max").first()?.value)
        assertEquals(2, prDao.observeRecordsInRange(CoreFixtures.USER_ID, 1735689500000, 1735689900000).first().size)
        prDao.delete(pr1)
        assertEquals(1, prDao.observeRecordsForUser(CoreFixtures.USER_ID).first().size)

        val challengeDao = database.challengeDao()
        val c1 = CoreFixtures.challenge(id = "c1", participantId = CoreFixtures.USER_TWO_ID)
        val c2 = CoreFixtures.challenge(id = "c2", participantId = CoreFixtures.USER_ID)
            .copy(status = "completed", creatorUserId = CoreFixtures.USER_TWO_ID)
        challengeDao.upsert(c1)
        challengeDao.upsertAll(listOf(c2))
        assertNotNull(challengeDao.observeChallenge("c1").first())
        assertEquals(1, challengeDao.observeCreatedChallenges(CoreFixtures.USER_ID).first().size)
        assertEquals(1, challengeDao.observeChallengesByStatus("active").first().size)
        assertTrue(challengeDao.observeChallengesForParticipant(CoreFixtures.USER_TWO_ID).first().isNotEmpty())
        challengeDao.delete(c1)
        assertNull(challengeDao.observeChallenge("c1").first())
    }
}
