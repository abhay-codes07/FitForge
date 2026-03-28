package com.fitforge.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitforge.app.data.local.db.entity.WorkoutTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutTemplateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WorkoutTemplateEntity)

    @Update
    suspend fun updateTemplate(template: WorkoutTemplateEntity)

    @Query("SELECT * FROM workout_templates WHERE userId = :userId ORDER BY useCount DESC, createdAtEpochMillis DESC")
    fun getTemplatesForUser(userId: String): Flow<List<WorkoutTemplateEntity>>

    @Query("SELECT * FROM workout_templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: String): WorkoutTemplateEntity?

    @Query("SELECT * FROM workout_templates WHERE isPublic = 1 ORDER BY useCount DESC LIMIT :limit")
    fun getPublicTemplates(limit: Int): Flow<List<WorkoutTemplateEntity>>

    @Query("DELETE FROM workout_templates WHERE id = :templateId")
    suspend fun deleteTemplate(templateId: String)
}
