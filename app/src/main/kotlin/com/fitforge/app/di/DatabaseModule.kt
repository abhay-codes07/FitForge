package com.fitforge.app.di

import android.content.Context
import androidx.room.Room
import com.fitforge.app.data.local.db.FitForgeDatabase
import com.fitforge.app.data.local.db.dao.AchievementDao
import com.fitforge.app.data.local.db.dao.BodyMeasurementDao
import com.fitforge.app.data.local.db.dao.ChallengeDao
import com.fitforge.app.data.local.db.dao.DailyLogDao
import com.fitforge.app.data.local.db.dao.ExerciseDao
import com.fitforge.app.data.local.db.dao.ExerciseSetDao
import com.fitforge.app.data.local.db.dao.GpsRoutePointDao
import com.fitforge.app.data.local.db.dao.PersonalRecordDao
import com.fitforge.app.data.local.db.dao.ProgramDao
import com.fitforge.app.data.local.db.dao.UserDao
import com.fitforge.app.data.local.db.dao.WorkoutDao
import com.fitforge.app.data.local.db.dao.WorkoutExerciseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideFitForgeDatabase(
        @ApplicationContext context: Context,
    ): FitForgeDatabase {
        return Room.databaseBuilder(
            context,
            FitForgeDatabase::class.java,
            "fitforge.db",
        )
            .createFromAsset("databases/exercises.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: FitForgeDatabase): UserDao = database.userDao()

    @Provides
    fun provideExerciseDao(database: FitForgeDatabase): ExerciseDao = database.exerciseDao()

    @Provides
    fun provideWorkoutDao(database: FitForgeDatabase): WorkoutDao = database.workoutDao()

    @Provides
    fun provideWorkoutExerciseDao(database: FitForgeDatabase): WorkoutExerciseDao = database.workoutExerciseDao()

    @Provides
    fun provideExerciseSetDao(database: FitForgeDatabase): ExerciseSetDao = database.exerciseSetDao()

    @Provides
    fun provideBodyMeasurementDao(database: FitForgeDatabase): BodyMeasurementDao = database.bodyMeasurementDao()

    @Provides
    fun provideDailyLogDao(database: FitForgeDatabase): DailyLogDao = database.dailyLogDao()

    @Provides
    fun provideAchievementDao(database: FitForgeDatabase): AchievementDao = database.achievementDao()

    @Provides
    fun provideProgramDao(database: FitForgeDatabase): ProgramDao = database.programDao()

    @Provides
    fun provideGpsRoutePointDao(database: FitForgeDatabase): GpsRoutePointDao = database.gpsRoutePointDao()

    @Provides
    fun providePersonalRecordDao(database: FitForgeDatabase): PersonalRecordDao = database.personalRecordDao()

    @Provides
    fun provideChallengeDao(database: FitForgeDatabase): ChallengeDao = database.challengeDao()
}
