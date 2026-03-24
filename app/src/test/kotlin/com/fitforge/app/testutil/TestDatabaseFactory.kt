package com.fitforge.app.testutil

import android.content.Context
import androidx.room.Room
import com.fitforge.app.data.local.db.FitForgeDatabase
import org.robolectric.RuntimeEnvironment

fun createInMemoryDatabase(): FitForgeDatabase {
    val context = RuntimeEnvironment.getApplication() as Context
    return Room.inMemoryDatabaseBuilder(context, FitForgeDatabase::class.java)
        .allowMainThreadQueries()
        .build()
}
