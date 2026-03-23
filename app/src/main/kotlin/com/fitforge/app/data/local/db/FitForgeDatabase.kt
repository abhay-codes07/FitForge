package com.fitforge.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fitforge.app.data.local.db.entity.DatabaseBootstrapEntity

@Database(
    entities = [DatabaseBootstrapEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class FitForgeDatabase : RoomDatabase()

