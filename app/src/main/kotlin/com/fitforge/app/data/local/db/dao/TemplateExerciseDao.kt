package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitforge.app.data.local.db.entity.TemplateExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<TemplateExerciseEntity>)

    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId ORDER BY orderIndex ASC")
    fun getExercisesForTemplate(templateId: String): Flow<List<TemplateExerciseEntity>>

    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId ORDER BY orderIndex ASC")
    suspend fun getExercisesForTemplateOnce(templateId: String): List<TemplateExerciseEntity>

    @Query("DELETE FROM template_exercises WHERE templateId = :templateId")
    suspend fun deleteExercisesForTemplate(templateId: String)
}
