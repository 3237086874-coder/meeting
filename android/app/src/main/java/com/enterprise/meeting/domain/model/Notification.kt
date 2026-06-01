package com.enterprise.meeting.domain.model

data class AppNotification(
    val id: Long,
    val type: NotificationType,
    val title: String,
    val body: String?,
    val referenceType: String?,
    val referenceId: Long?,
    val isRead: Boolean = false,
    val createdAt: Long
)

enum class NotificationType {
    TASK_ASSIGNED,
    REVIEW_REQUIRED,
    MEETING_PUBLISHED,
    TASK_COMPLETED,
    AI_PROCESSING_DONE,
    AI_PROCESSING_FAILED,
    MEMO_REMINDER
}
