package com.enterprise.meeting.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memos")
data class MemoEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "user_id") val userId: Long,
    val title: String,
    val content: String?,
    val priority: String = "NORMAL",
    @ColumnInfo(name = "reminder_at") val reminderAt: Long?,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    val color: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
