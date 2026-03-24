package com.fitforge.app.testutil

import com.fitforge.app.data.local.db.entity.AchievementEntity
import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import com.fitforge.app.data.local.db.entity.ChallengeEntity
import com.fitforge.app.data.local.db.entity.DailyLogEntity
import com.fitforge.app.data.local.db.entity.ExerciseEntity
import com.fitforge.app.data.local.db.entity.ExerciseSetEntity
import com.fitforge.app.data.local.db.entity.GpsRoutePointEntity
import com.fitforge.app.data.local.db.entity.PersonalRecordEntity
import com.fitforge.app.data.local.db.entity.ProgramEntity
import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity

object CoreFixtures {
    const val USER_ID = "user_1"
    const val USER_TWO_ID = "user_2"
    const val EXERCISE_ID = "exercise_1"
    const val EXERCISE_TWO_ID = "exercise_2"
    const val WORKOUT_ID = "workout_1"
    const val WORKOUT_TWO_ID = "workout_2"
    const val WORKOUT_EXERCISE_ID = "workout_exercise_1"
    const val RECORD_ID = "record_1"
    const val CHALLENGE_ID = "challenge_1"

    private const val NOW = 1735689600000L

    fun user(id: String = USER_ID, createdAtOffset: Long = 0L): UserEntity = UserEntity(
        id = id,
        displayName = "User $id",
        email = "$id@fitforge.dev",
        photoUrl = null,
        birthDateEpochMillis = null,
        gender = "unspecified",
        heightCm = 178f,
        currentWeightKg = 80f,
        targetWeightKg = 75f,
        fitnessLevel = "intermediate",
        primaryGoals = setOf("stay_fit"),
        workoutLocations = setOf("gym"),
        availableEquipment = setOf("dumbbell"),
        preferredUnitSystem = "metric",
        isNotificationEnabled = true,
        isHealthConnectLinked = false,
        isPremium = false,
        createdAtEpochMillis = NOW + createdAtOffset,
        updatedAtEpochMillis = NOW + createdAtOffset,
    )

    fun exercise(
        id: String = EXERCISE_ID,
        category: String = "strength",
        difficulty: String = "intermediate",
        isPremium: Boolean = false,
    ): ExerciseEntity = ExerciseEntity(
        id = id,
        name = "Exercise $id",
        description = "Exercise description",
        category = category,
        difficulty = difficulty,
        equipment = setOf("dumbbell"),
        primaryMuscles = setOf("chest"),
        secondaryMuscles = setOf("triceps"),
        instructions = listOf("Step 1", "Step 2"),
        estimatedDurationSeconds = 90,
        estimatedCalories = 30,
        imageUrl = null,
        videoUrl = null,
        isPremium = isPremium,
        source = "test",
        createdAtEpochMillis = NOW,
        updatedAtEpochMillis = NOW,
    )

    fun workout(
        id: String = WORKOUT_ID,
        userId: String = USER_ID,
        scheduledDate: Long? = NOW + 10_000,
        completedAt: Long? = null,
        status: String = "scheduled",
    ): WorkoutEntity = WorkoutEntity(
        id = id,
        userId = userId,
        name = "Workout $id",
        description = "Workout description",
        workoutType = "strength",
        difficulty = "intermediate",
        estimatedDurationMinutes = 45,
        estimatedCalories = 350,
        source = "test",
        scheduledDateEpochMillis = scheduledDate,
        completedAtEpochMillis = completedAt,
        status = status,
        notes = null,
        createdAtEpochMillis = NOW,
        updatedAtEpochMillis = NOW,
    )

    fun workoutExercise(
        id: String = WORKOUT_EXERCISE_ID,
        workoutId: String = WORKOUT_ID,
        exerciseId: String = EXERCISE_ID,
        sequenceIndex: Int = 0,
    ): WorkoutExerciseEntity = WorkoutExerciseEntity(
        id = id,
        workoutId = workoutId,
        exerciseId = exerciseId,
        sequenceIndex = sequenceIndex,
        targetSets = 3,
        targetRepsMin = 8,
        targetRepsMax = 12,
        targetDurationSeconds = null,
        targetDistanceMeters = null,
        targetWeightKg = 20f,
        restDurationSeconds = 90,
        supersetGroup = null,
        notes = null,
        isOptional = false,
    )

    fun exerciseSet(
        id: String = "set_1",
        workoutId: String = WORKOUT_ID,
        workoutExerciseId: String = WORKOUT_EXERCISE_ID,
        exerciseId: String = EXERCISE_ID,
        setNumber: Int = 1,
    ): ExerciseSetEntity = ExerciseSetEntity(
        id = id,
        workoutId = workoutId,
        workoutExerciseId = workoutExerciseId,
        exerciseId = exerciseId,
        setNumber = setNumber,
        reps = 10,
        weightKg = 20f,
        durationSeconds = null,
        distanceMeters = null,
        restDurationSeconds = 90,
        rpe = 7f,
        isWarmUp = false,
        completedAtEpochMillis = NOW,
        notes = null,
    )

    fun measurement(id: String = "measurement_1", userId: String = USER_ID, recordedAt: Long = NOW): BodyMeasurementEntity = BodyMeasurementEntity(
        id = id,
        userId = userId,
        recordedAtEpochMillis = recordedAt,
        weightKg = 79.5f,
        bodyFatPercent = 18f,
        chestCm = 98f,
        waistCm = 84f,
        hipsCm = 96f,
        bicepsCm = 34f,
        thighCm = 55f,
        calfCm = 37f,
        neckCm = 39f,
        progressPhotoUris = listOf("uri://progress"),
        notes = null,
    )

    fun dailyLog(id: String = "log_1", userId: String = USER_ID, epochDay: Long = 1000L): DailyLogEntity = DailyLogEntity(
        id = id,
        userId = userId,
        logDateEpochDay = epochDay,
        steps = 8000,
        activeCalories = 450,
        workoutMinutes = 50,
        waterIntakeMl = 2200,
        sleepMinutes = 430,
        distanceMeters = 6200f,
        restingHeartRate = 58,
        averageHeartRate = 108,
        readinessScore = 74,
        notes = null,
    )

    fun achievement(id: String = "achievement_1", userId: String = USER_ID, unlockedAt: Long? = NOW): AchievementEntity = AchievementEntity(
        id = id,
        userId = userId,
        title = "Consistency",
        description = "Complete 10 workouts",
        category = "streak",
        iconName = "bolt",
        progress = 10f,
        target = 10f,
        unlockedAtEpochMillis = unlockedAt,
        isClaimed = unlockedAt != null,
    )

    fun program(id: String = "program_1", goal: String = "fat_loss", premium: Boolean = false): ProgramEntity = ProgramEntity(
        id = id,
        name = "Program $id",
        description = "Program description",
        goal = goal,
        difficulty = "intermediate",
        durationWeeks = 8,
        workoutIds = listOf(WORKOUT_ID),
        coverImageUrl = null,
        isPremium = premium,
        createdAtEpochMillis = NOW,
        updatedAtEpochMillis = NOW,
    )

    fun gpsPoint(id: Long = 0L, workoutId: String = WORKOUT_ID, ts: Long = NOW): GpsRoutePointEntity = GpsRoutePointEntity(
        id = id,
        workoutId = workoutId,
        latitude = 12.9716,
        longitude = 77.5946,
        altitudeMeters = null,
        accuracyMeters = 5f,
        speedMetersPerSecond = 2.8f,
        heartRate = 132,
        timestampEpochMillis = ts,
        segmentIndex = 0,
    )

    fun personalRecord(id: String = RECORD_ID, userId: String = USER_ID, exerciseId: String = EXERCISE_ID, workoutId: String = WORKOUT_ID): PersonalRecordEntity =
        PersonalRecordEntity(
            id = id,
            userId = userId,
            exerciseId = exerciseId,
            workoutId = workoutId,
            metricType = "one_rep_max",
            value = 105.0,
            unit = "kg",
            achievedAtEpochMillis = NOW,
            notes = null,
        )

    fun challenge(id: String = CHALLENGE_ID, creatorUserId: String = USER_ID, participantId: String = USER_TWO_ID): ChallengeEntity = ChallengeEntity(
        id = id,
        creatorUserId = creatorUserId,
        title = "30-Day Steps",
        description = "Beat 300k steps",
        challengeType = "steps",
        goalValue = 300000.0,
        unit = "steps",
        participantIds = setOf(creatorUserId, participantId),
        winnerUserId = null,
        status = "active",
        startAtEpochMillis = NOW,
        endAtEpochMillis = NOW + 30 * 24 * 60 * 60 * 1000,
        createdAtEpochMillis = NOW,
    )
}
