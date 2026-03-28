package com.fitforge.app.domain.usecase.programs

import com.fitforge.app.data.local.db.entity.ProgramEntity
import com.fitforge.app.domain.repository.ProgramRepository
import com.fitforge.app.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetProgramsUseCase @Inject constructor(
    private val programRepository: ProgramRepository,
) {
    operator fun invoke(): Flow<List<ProgramEntity>> {
        return programRepository.observePrograms()
    }

    operator fun invoke(goal: String): Flow<List<ProgramEntity>> {
        return programRepository.observeProgramsByGoal(goal)
    }
}

class GetProgramDetailUseCase @Inject constructor(
    private val programRepository: ProgramRepository,
) {
    operator fun invoke(programId: String): Flow<ProgramEntity?> {
        return programRepository.observeProgram(programId)
    }
}

class GetFreeProgramsUseCase @Inject constructor(
    private val programRepository: ProgramRepository,
) {
    operator fun invoke(): Flow<List<ProgramEntity>> {
        return programRepository.observeProgramsByPremiumState(isPremium = false)
    }
}

class GetPremiumProgramsUseCase @Inject constructor(
    private val programRepository: ProgramRepository,
) {
    operator fun invoke(): Flow<List<ProgramEntity>> {
        return programRepository.observeProgramsByPremiumState(isPremium = true)
    }
}

class CanAccessProgramUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(program: ProgramEntity): Result<Boolean> {
        return runCatching {
            if (!program.isPremium) {
                return@runCatching true
            }
            val user = userRepository.getPrimaryUser()
            user?.isPremium ?: false
        }
    }
}
