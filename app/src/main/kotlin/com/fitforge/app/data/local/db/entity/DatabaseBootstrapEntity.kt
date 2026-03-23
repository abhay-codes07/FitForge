package com.fitforge.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "database_bootstrap")
data class DatabaseBootstrapEntity(
    @PrimaryKey val id: Int = 1,
    val createdAtEpochMillis: Long = 0L,
)
