package com.enterprise.meeting.domain.model

data class Meeting(
    val id: Long,
    val title: String,
    val description: String?,
    val meetingTime: Long,
    val location: String?,
    val durationMinutes: Int?,
    val createdBy: Long,
    val createdByName: String?,
    val assignedReviewer: Long?,
    val assignedReviewerName: String?,
    val state: MeetingState,
    val stateMetadata: Map<String, String> = emptyMap(),
    val aiRetryCount: Int = 0,
    val aiError: String?,
    val createdAt: Long,
    val updatedAt: Long
)

enum class MeetingState {
    PENDING,
    AI_PROCESSING,
    PENDING_REVIEW,
    REJECTED,
    PENDING_PUBLISH,
    PUBLISHED,
    ARCHIVED;

    val displayName: String
        get() = when (this) {
            PENDING -> "执行中"
            AI_PROCESSING -> "AI处理中"
            PENDING_REVIEW -> "待审核"
            REJECTED -> "已驳回"
            PENDING_PUBLISH -> "待发布"
            PUBLISHED -> "已发布"
            ARCHIVED -> "已归档"
        }
}
