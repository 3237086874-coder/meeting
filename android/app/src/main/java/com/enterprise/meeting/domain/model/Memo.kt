package com.enterprise.meeting.domain.model

data class Memo(
    val id: Long,
    val title: String,
    val content: String?,
    val priority: String = "NORMAL",
    val reminderAt: Long?,
    val isCompleted: Boolean = false,
    val color: String?,
    val createdAt: Long,
    val updatedAt: Long
)
