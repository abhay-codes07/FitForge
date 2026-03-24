package com.fitforge.app.di

import android.content.Context
import androidx.room.Room
import com.fitforge.app.data.local.db.FitForgeDatabase
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
}
