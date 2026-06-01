package com.enterprise.meeting.data.local.dao

import androidx.room.*
import com.enterprise.meeting.data.local.entity.PendingActionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingActionDao {
    @Query("SELECT * FROM pending_actions WHERE status = 'PENDING' ORDER BY created_at ASC")
    fun getPendingActions(): Flow<List<PendingActionEntity>>

    @Insert
    suspend fun insert(action: PendingActionEntity)

    @Update
    suspend fun update(action: PendingActionEntity)

    @Query("UPDATE pending_actions SET status = :status, last_error = :error WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, error: String? = null)

    @Delete
    suspend fun delete(action: PendingActionEntity)

    @Query("DELETE FROM pending_actions WHERE status = 'COMPLETED'")
    suspend fun deleteCompleted()
}
