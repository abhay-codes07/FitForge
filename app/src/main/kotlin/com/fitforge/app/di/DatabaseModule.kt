package com.fitforge.app.di

import android.content.Context
import androidx.room.Room
import com.fitforge.app.data.local.db.FitForgeDatabase
import com.fitforge.app.data.local.db.dao.AchievementDao
import com.fitforge.app.data.local.db.dao.ActivityCommentDao
import com.fitforge.app.data.local.db.dao.ActivityFeedDao
import com.fitforge.app.data.local.db.dao.BodyMeasurementDao
import com.fitforge.app.data.local.db.dao.ChallengeDao
import com.fitforge.app.data.local.db.dao.DailyLogDao
import com.fitforge.app.data.local.db.dao.ExerciseDao
import com.fitforge.app.data.local.db.dao.ExerciseSetDao
import com.fitforge.app.data.local.db.dao.FoodItemDao
import com.fitforge.app.data.local.db.dao.FriendDao
import com.fitforge.app.data.local.db.dao.GpsRoutePointDao
import com.fitforge.app.data.local.db.dao.MealDao
import com.fitforge.app.data.local.db.dao.NutritionGoalDao
import com.fitforge.app.data.local.db.dao.PersonalRecordDao
import com.fitforge.app.data.local.db.dao.ProgramDao
import com.fitforge.app.data.local.db.dao.RecoveryLogDao
import com.fitforge.app.data.local.db.dao.TemplateExerciseDao
import com.fitforge.app.data.local.db.dao.UserDao
import com.fitforge.app.data.local.db.dao.WorkoutDao
import com.fitforge.app.data.local.db.dao.WorkoutExerciseDao
import com.fitforge.app.data.local.db.dao.WorkoutTemplateDao
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

    @Provides
    fun provideFriendDao(database: FitForgeDatabase): FriendDao = database.friendDao()

    @Provides
    fun provideActivityFeedDao(database: FitForgeDatabase): ActivityFeedDao = database.activityFeedDao()

    @Provides
    fun provideActivityCommentDao(database: FitForgeDatabase): ActivityCommentDao = database.activityCommentDao()

    @Provides
    fun provideMealDao(database: FitForgeDatabase): MealDao = database.mealDao()

    @Provides
    fun provideFoodItemDao(database: FitForgeDatabase): FoodItemDao = database.foodItemDao()

    @Provides
    fun provideNutritionGoalDao(database: FitForgeDatabase): NutritionGoalDao = database.nutritionGoalDao()

    @Provides
    fun provideWorkoutTemplateDao(database: FitForgeDatabase): WorkoutTemplateDao = database.workoutTemplateDao()

    @Provides
    fun provideTemplateExerciseDao(database: FitForgeDatabase): TemplateExerciseDao = database.templateExerciseDao()

    @Provides
    fun provideRecoveryLogDao(database: FitForgeDatabase): RecoveryLogDao = database.recoveryLogDao()
}
