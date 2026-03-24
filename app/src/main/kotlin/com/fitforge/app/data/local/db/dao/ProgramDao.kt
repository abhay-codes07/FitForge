package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fitforge.app.data.local.db.entity.ProgramEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDao {
    @Query("SELECT * FROM programs ORDER BY name COLLATE NOCASE ASC")
    fun observePrograms(): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE id = :programId LIMIT 1")
    fun observeProgram(programId: String): Flow<ProgramEntity?>

    @Query("SELECT * FROM programs WHERE goal = :goal ORDER BY difficulty COLLATE NOCASE ASC, name COLLATE NOCASE ASC")
    fun observeProgramsByGoal(goal: String): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE isPremium = :isPremium ORDER BY name COLLATE NOCASE ASC")
    fun observeProgramsByPremiumState(isPremium: Boolean): Flow<List<ProgramEntity>>

    @Upsert
    suspend fun upsert(program: ProgramEntity)

    @Upsert
    suspend fun upsertAll(programs: List<ProgramEntity>)

    @Delete
    suspend fun delete(program: ProgramEntity)
}
