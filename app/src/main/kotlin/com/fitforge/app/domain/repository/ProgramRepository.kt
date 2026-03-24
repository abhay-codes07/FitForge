package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.ProgramEntity
import kotlinx.coroutines.flow.Flow

interface ProgramRepository {
    fun observePrograms(): Flow<List<ProgramEntity>>
    fun observeProgram(programId: String): Flow<ProgramEntity?>
    fun observeProgramsByGoal(goal: String): Flow<List<ProgramEntity>>
    fun observeProgramsByPremiumState(isPremium: Boolean): Flow<List<ProgramEntity>>
    suspend fun upsertProgram(program: ProgramEntity)
    suspend fun upsertPrograms(programs: List<ProgramEntity>)
    suspend fun deleteProgram(program: ProgramEntity)
}
