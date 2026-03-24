package com.fitforge.app.data.repository

import com.fitforge.app.data.local.db.dao.ProgramDao
import com.fitforge.app.data.local.db.entity.ProgramEntity
import com.fitforge.app.domain.repository.ProgramRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ProgramRepositoryImpl @Inject constructor(
    private val programDao: ProgramDao,
) : ProgramRepository {
    override fun observePrograms(): Flow<List<ProgramEntity>> = programDao.observePrograms()

    override fun observeProgram(programId: String): Flow<ProgramEntity?> = programDao.observeProgram(programId)

    override fun observeProgramsByGoal(goal: String): Flow<List<ProgramEntity>> = programDao.observeProgramsByGoal(goal)

    override fun observeProgramsByPremiumState(isPremium: Boolean): Flow<List<ProgramEntity>> = programDao.observeProgramsByPremiumState(isPremium)

    override suspend fun upsertProgram(program: ProgramEntity) = programDao.upsert(program)

    override suspend fun upsertPrograms(programs: List<ProgramEntity>) = programDao.upsertAll(programs)

    override suspend fun deleteProgram(program: ProgramEntity) = programDao.delete(program)
}
