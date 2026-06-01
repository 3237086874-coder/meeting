package com.enterprise.meeting.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meetings")
data class MeetingEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "meeting_time") val meetingTime: Long,
    val location: String?,
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Int?,
    @ColumnInfo(name = "created_by") val createdBy: Long,
    @ColumnInfo(name = "assigned_reviewer") val assignedReviewer: Long?,
    val state: String,
    @ColumnInfo(name = "ai_error") val aiError: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
