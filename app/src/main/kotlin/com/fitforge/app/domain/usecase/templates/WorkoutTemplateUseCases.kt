package com.fitforge.app.domain.usecase.templates

import com.fitforge.app.data.local.db.dao.TemplateExerciseDao
import com.fitforge.app.data.local.db.dao.WorkoutTemplateDao
import com.fitforge.app.data.local.db.entity.TemplateExerciseEntity
import com.fitforge.app.data.local.db.entity.WorkoutEntity
import com.fitforge.app.data.local.db.entity.WorkoutExerciseEntity
import com.fitforge.app.data.local.db.entity.WorkoutTemplateEntity
import com.fitforge.app.domain.repository.UserRepository
import com.fitforge.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

class SaveWorkoutAsTemplateUseCase @Inject constructor(
    private val workoutTemplateDao: WorkoutTemplateDao,
    private val templateExerciseDao: TemplateExerciseDao,
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        workoutId: String,
        templateName: String,
        isPublic: Boolean = false,
    ): Result<WorkoutTemplateEntity> = runCatching {
        val currentUser = userRepository.getPrimaryUser()
            ?: throw IllegalStateException("User not logged in")

        val workout = workoutRepository.getWorkoutById(workoutId)
            ?: throw IllegalArgumentException("Workout not found")

        val workoutExercises = workoutRepository.getExercisesForWorkoutOnce(workoutId)

        val template = WorkoutTemplateEntity(
            id = UUID.randomUUID().toString(),
            userId = currentUser.id,
            name = templateName,
            description = workout.description,
            workoutType = workout.workoutType,
            difficulty = workout.difficulty,
            estimatedDurationMinutes = workout.estimatedDurationMinutes,
            estimatedCalories = workout.estimatedCalories,
            isPublic = isPublic,
            useCount = 0,
            createdAtEpochMillis = System.currentTimeMillis(),
            updatedAtEpochMillis = System.currentTimeMillis(),
        )

        workoutTemplateDao.insertTemplate(template)

        val templateExercises = workoutExercises.mapIndexed { index, workoutExercise ->
            TemplateExerciseEntity(
                id = UUID.randomUUID().toString(),
                templateId = template.id,
                exerciseId = workoutExercise.exerciseId,
                exerciseName = workoutExercise.exerciseName,
                orderIndex = index,
                targetSets = workoutExercise.targetSets,
                targetReps = workoutExercise.targetReps,
                targetWeight = workoutExercise.targetWeight,
                restSeconds = workoutExercise.restSeconds,
                notes = workoutExercise.notes,
            )
        }

        if (templateExercises.isNotEmpty()) {
            templateExerciseDao.insertExercises(templateExercises)
        }

        template
    }
}

class GetMyTemplatesUseCase @Inject constructor(
    private val workoutTemplateDao: WorkoutTemplateDao,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<WorkoutTemplateEntity>> {
        val currentUser = userRepository.getPrimaryUser() ?: return flowOf(emptyList())
        return workoutTemplateDao.getTemplatesForUser(currentUser.id)
    }
}

class GetPublicTemplatesUseCase @Inject constructor(
    private val workoutTemplateDao: WorkoutTemplateDao,
) {
    operator fun invoke(limit: Int = 20): Flow<List<WorkoutTemplateEntity>> {
        return workoutTemplateDao.getPublicTemplates(limit)
    }
}

class CreateWorkoutFromTemplateUseCase @Inject constructor(
    private val workoutTemplateDao: WorkoutTemplateDao,
    private val templateExerciseDao: TemplateExerciseDao,
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(templateId: String): Result<WorkoutEntity> = runCatching {
        val currentUser = userRepository.getPrimaryUser()
            ?: throw IllegalStateException("User not logged in")

        val template = workoutTemplateDao.getTemplateById(templateId)
            ?: throw IllegalArgumentException("Template not found")

        val templateExercises = templateExerciseDao.getExercisesForTemplateOnce(templateId)

        val nowMillis = System.currentTimeMillis()
        val workout = WorkoutEntity(
            id = UUID.randomUUID().toString(),
            userId = currentUser.id,
            name = template.name,
            description = template.description,
            workoutType = template.workoutType,
            difficulty = template.difficulty,
            estimatedDurationMinutes = template.estimatedDurationMinutes,
            estimatedCalories = template.estimatedCalories,
            status = "planned",
            scheduledDateEpochMillis = null,
            startedAtEpochMillis = null,
            completedAtEpochMillis = null,
            actualDurationMinutes = null,
            createdAtEpochMillis = nowMillis,
            updatedAtEpochMillis = nowMillis,
        )

        workoutRepository.upsertWorkout(workout)

        val workoutExercises = templateExercises.mapIndexed { index, templateExercise ->
            WorkoutExerciseEntity(
                id = UUID.randomUUID().toString(),
                workoutId = workout.id,
                exerciseId = templateExercise.exerciseId,
                exerciseName = templateExercise.exerciseName,
                orderIndex = index,
                targetSets = templateExercise.targetSets,
                targetReps = templateExercise.targetReps,
                targetWeight = templateExercise.targetWeight,
                actualSets = 0,
                isCompleted = false,
                restSeconds = templateExercise.restSeconds,
                notes = templateExercise.notes,
            )
        }

        workoutRepository.insertWorkoutExercises(workoutExercises)

        // Increment use count
        val updatedTemplate = template.copy(useCount = template.useCount + 1)
        workoutTemplateDao.updateTemplate(updatedTemplate)

        workout
    }
}

class DeleteTemplateUseCase @Inject constructor(
    private val workoutTemplateDao: WorkoutTemplateDao,
    private val templateExerciseDao: TemplateExerciseDao,
) {
    suspend operator fun invoke(templateId: String): Result<Unit> = runCatching {
        templateExerciseDao.deleteExercisesForTemplate(templateId)
        workoutTemplateDao.deleteTemplate(templateId)
    }
}

class GetTemplateExercisesUseCase @Inject constructor(
    private val templateExerciseDao: TemplateExerciseDao,
) {
    operator fun invoke(templateId: String): Flow<List<TemplateExerciseEntity>> {
        return templateExerciseDao.getExercisesForTemplate(templateId)
    }
}
