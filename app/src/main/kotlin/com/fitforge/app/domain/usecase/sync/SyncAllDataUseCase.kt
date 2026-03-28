package com.fitforge.app.domain.usecase.sync

import com.fitforge.app.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Syncs all user data from cloud on app startup or manual refresh.
 * Runs all sync operations in parallel for optimal performance.
 */
class SyncAllDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val pullWorkoutsFromCloudUseCase: PullWorkoutsFromCloudUseCase,
    private val pullUserFromCloudUseCase: PullUserFromCloudUseCase,
) {
    suspend operator fun invoke(): Result<SyncResult> {
        return runCatching {
            val user = userRepository.getPrimaryUser()
            if (user == null) {
                return@runCatching SyncResult(
                    workoutsSynced = 0,
                    userSynced = false,
                    error = null,
                )
            }

            coroutineScope {
                // Run all sync operations in parallel
                val workoutsDeferred = async { pullWorkoutsFromCloudUseCase() }
                val userDeferred = async { pullUserFromCloudUseCase(user.id) }

                // Wait for all operations to complete
                val workoutsResult = workoutsDeferred.await()
                val userResult = userDeferred.await()

                SyncResult(
                    workoutsSynced = workoutsResult.getOrElse { 0 },
                    userSynced = userResult.getOrElse { false },
                    error = when {
                        workoutsResult.isFailure -> workoutsResult.exceptionOrNull()?.message
                        userResult.isFailure -> userResult.exceptionOrNull()?.message
                        else -> null
                    },
                )
            }
        }
    }
}

data class SyncResult(
    val workoutsSynced: Int,
    val userSynced: Boolean,
    val error: String?,
)
