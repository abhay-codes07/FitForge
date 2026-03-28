package com.fitforge.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fitforge.app.data.local.db.converter.RoomTypeConverters
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
import com.fitforge.app.data.local.db.entity.AchievementEntity
import com.fitforge.app.data.local.db.entity.ActivityCommentEntity
import com.fitforge.app.data.local.db.entity.ActivityFeedItemEntity
import com.fitforge.app.data.local.db.entity.BodyMeasurementEntity
import com.fitforge.app.data.local.db.entity.ChallengeEntity
import com.fitforge.app.data.local.db.entity.DailyLogEntity
import com.fitforge.app.data.local.db.entity.ExerciseEntity
import com.fitforge.app.data.local.db.entity.ExerciseSetEntity
import com.fitforge.app.data.local.db.entity.FoodItemEntity
import com.fitforge.app.data.local.db.entity.FriendEntity
import com.fitforge.app.data.local.db.entity.GpsRoutePointEntity
import com.fitforge.app.data.local.db.entity.MealEntity
import com.fitforge.app.data.local.db.entity.NutritionGoalEntity
import com.fitforge.app.data.local.db.entity.PersonalRecordEntity
import com.fitforge.app.data.local.db.entity.ProgramEntity
import com.fitforge.app.data.local.db.entity.RecoveryLogEntity
import com.fitforge.app.data.local.db.entity.TemplateExerciseEntity
import com.fitforge.app.data.local.db.entity.UserEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity
import com.fitforge.app.data.local.db.entity.WorkoutNoteEntity
import com.fitforge.app.data.local.db.entity.WorkoutShareEntity
import com.fitforge.app.data.local.db.entity.WorkoutTemplateEntity

@Database(
    entities = [
        UserEntity::class,
        ExerciseEntity::class,
        WorkoutEntity::class,
        WorkoutExerciseEntity::class,
        ExerciseSetEntity::class,
        BodyMeasurementEntity::class,
        DailyLogEntity::class,
        AchievementEntity::class,
        ProgramEntity::class,
        GpsRoutePointEntity::class,
        PersonalRecordEntity::class,
        ChallengeEntity::class,
        FriendEntity::class,
        ActivityFeedItemEntity::class,
        WorkoutShareEntity::class,
        ActivityCommentEntity::class,
        MealEntity::class,
        FoodItemEntity::class,
        NutritionGoalEntity::class,
        WorkoutTemplateEntity::class,
        TemplateExerciseEntity::class,
        WorkoutNoteEntity::class,
        RecoveryLogEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
@TypeConverters(RoomTypeConverters::class)
abstract class FitForgeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun exerciseSetDao(): ExerciseSetDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun achievementDao(): AchievementDao
    abstract fun programDao(): ProgramDao
    abstract fun gpsRoutePointDao(): GpsRoutePointDao
    abstract fun personalRecordDao(): PersonalRecordDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun friendDao(): FriendDao
    abstract fun activityFeedDao(): ActivityFeedDao
    abstract fun activityCommentDao(): ActivityCommentDao
    abstract fun mealDao(): MealDao
    abstract fun foodItemDao(): FoodItemDao
    abstract fun nutritionGoalDao(): NutritionGoalDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun templateExerciseDao(): TemplateExerciseDao
    abstract fun recoveryLogDao(): RecoveryLogDao
}
