package com.fitforge.app.domain.repository

import com.fitforge.app.data.local.db.entity.PersonalRecordEntity

interface CloudPersonalRecordSyncRepository {
    suspend fun pushPersonalRecord(record: PersonalRecordEntity): Result<Unit>
    suspend fun pullPersonalRecordsForUser(userId: String): Result<List<PersonalRecordEntity>>
}
