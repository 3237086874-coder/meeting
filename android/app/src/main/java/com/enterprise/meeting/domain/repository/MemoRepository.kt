package com.enterprise.meeting.domain.repository

import com.enterprise.meeting.domain.model.Memo

interface MemoRepository {
    suspend fun getMemos(userId: Long): Result<List<Memo>>
    suspend fun createMemo(
        userId: Long,
        title: String,
        content: String?,
        reminderAt: Long?,
        color: String?
    ): Result<Memo>
    suspend fun updateMemo(
        id: Long,
        title: String?,
        content: String?,
        isCompleted: Boolean?
    ): Result<Memo>
    suspend fun deleteMemo(id: Long): Result<Unit>
}
