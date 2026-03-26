package com.fitforge.app.di

import com.fitforge.app.data.repository.AchievementRepositoryImpl
import com.fitforge.app.data.repository.BodyMeasurementRepositoryImpl
import com.fitforge.app.data.repository.ChallengeRepositoryImpl
import com.fitforge.app.data.repository.DailyLogRepositoryImpl
import com.fitforge.app.data.repository.ExerciseRepositoryImpl
import com.fitforge.app.data.repository.ExerciseSetRepositoryImpl
import com.fitforge.app.data.repository.FirebaseAuthRepositoryImpl
import com.fitforge.app.data.repository.GpsRoutePointRepositoryImpl
import com.fitforge.app.data.repository.OnboardingRepositoryImpl
import com.fitforge.app.data.repository.PersonalRecordRepositoryImpl
import com.fitforge.app.data.repository.ProgramRepositoryImpl
import com.fitforge.app.data.repository.UserRepositoryImpl
import com.fitforge.app.data.repository.WelcomeRepositoryImpl
import com.fitforge.app.data.repository.WorkoutExerciseRepositoryImpl
import com.fitforge.app.data.repository.WorkoutRepositoryImpl
import com.fitforge.app.domain.repository.AchievementRepository
import com.fitforge.app.domain.repository.AuthRepository
import com.fitforge.app.domain.repository.BodyMeasurementRepository
import com.fitforge.app.domain.repository.ChallengeRepository
import com.fitforge.app.domain.repository.DailyLogRepository
import com.fitforge.app.domain.repository.ExerciseRepository
import com.fitforge.app.domain.repository.ExerciseSetRepository
import com.fitforge.app.domain.repository.GpsRoutePointRepository
import com.fitforge.app.domain.repository.OnboardingRepository
import com.fitforge.app.domain.repository.PersonalRecordRepository
import com.fitforge.app.domain.repository.ProgramRepository
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WelcomeRepository
import com.fitforge.app.domain.repository.WorkoutExerciseRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: FirebaseAuthRepositoryImpl,
    ): AuthRepository

    @Binds
    abstract fun bindOnboardingRepository(
        onboardingRepositoryImpl: OnboardingRepositoryImpl,
    ): OnboardingRepository

    @Binds
    abstract fun bindWelcomeRepository(
        welcomeRepositoryImpl: WelcomeRepositoryImpl,
    ): WelcomeRepository

    @Binds
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl,
    ): UserRepository

    @Binds
    abstract fun bindExerciseRepository(
        exerciseRepositoryImpl: ExerciseRepositoryImpl,
    ): ExerciseRepository

    @Binds
    abstract fun bindWorkoutRepository(
        workoutRepositoryImpl: WorkoutRepositoryImpl,
    ): WorkoutRepository

    @Binds
    abstract fun bindWorkoutExerciseRepository(
        workoutExerciseRepositoryImpl: WorkoutExerciseRepositoryImpl,
    ): WorkoutExerciseRepository

    @Binds
    abstract fun bindExerciseSetRepository(
        exerciseSetRepositoryImpl: ExerciseSetRepositoryImpl,
    ): ExerciseSetRepository

    @Binds
    abstract fun bindBodyMeasurementRepository(
        bodyMeasurementRepositoryImpl: BodyMeasurementRepositoryImpl,
    ): BodyMeasurementRepository

    @Binds
    abstract fun bindDailyLogRepository(
        dailyLogRepositoryImpl: DailyLogRepositoryImpl,
    ): DailyLogRepository

    @Binds
    abstract fun bindAchievementRepository(
        achievementRepositoryImpl: AchievementRepositoryImpl,
    ): AchievementRepository

    @Binds
    abstract fun bindProgramRepository(
        programRepositoryImpl: ProgramRepositoryImpl,
    ): ProgramRepository

    @Binds
    abstract fun bindGpsRoutePointRepository(
        gpsRoutePointRepositoryImpl: GpsRoutePointRepositoryImpl,
    ): GpsRoutePointRepository

    @Binds
    abstract fun bindPersonalRecordRepository(
        personalRecordRepositoryImpl: PersonalRecordRepositoryImpl,
    ): PersonalRecordRepository

    @Binds
    abstract fun bindChallengeRepository(
        challengeRepositoryImpl: ChallengeRepositoryImpl,
    ): ChallengeRepository
}
