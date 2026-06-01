package com.enterprise.meeting.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "meeting_id") val meetingId: Long,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "assigned_to") val assignedTo: Long,
    @ColumnInfo(name = "assigned_by") val assignedBy: Long?,
    val priority: String,
    @ColumnInfo(name = "due_date") val dueDate: String?,
    val state: String,
    @ColumnInfo(name = "completion_note") val completionNote: String?,
    @ColumnInfo(name = "is_visible") val isVisible: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "completed_at") val completedAt: Long?
)
