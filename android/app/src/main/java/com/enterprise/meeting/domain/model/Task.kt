package com.enterprise.meeting.domain.model

data class Task(
    val id: Long,
    val meetingId: Long,
    val meetingTitle: String?,
    val title: String,
    val description: String?,
    val assignedTo: Long,
    val assignedToName: String?,
    val assignedBy: Long?,
    val assignedByName: String?,
    val priority: TaskPriority,
    val dueDate: String?,
    val state: TaskState,
    val stateMetadata: Map<String, String> = emptyMap(),
    val completionNote: String?,
    val isAiExtracted: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long,
    val completedAt: Long?
)

enum class TaskState {
    PENDING_CONFIRM,
    EXECUTING,
    COMPLETED,
    REJECTED;

    val displayName: String
        get() = when (this) {
            PENDING_CONFIRM -> "待确认"
            EXECUTING -> "执行中"
            COMPLETED -> "已完成"
            REJECTED -> "已拒绝"
        }
}

enum class TaskPriority {
    LOW, MEDIUM, HIGH, URGENT;

    val displayName: String
        get() = when (this) {
            LOW -> "低"
            MEDIUM -> "中"
            HIGH -> "高"
            URGENT -> "紧急"
        }
}
