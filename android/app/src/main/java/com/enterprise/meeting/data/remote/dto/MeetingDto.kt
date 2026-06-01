package com.enterprise.meeting.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MeetingDto(
    val id: Long = 0,
    val title: String = "",
    val description: String? = null,
    val meetingTime: String? = null,
    val location: String? = null,
    val durationMinutes: Int? = null,
    val createdBy: Long = 0,
    val assignedReviewer: Long? = null,
    val state: String = "PENDING",
    val stateMetadata: Map<String, String> = emptyMap(),
    val aiRetryCount: Int = 0,
    val aiError: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class TaskDto(
    val id: Long = 0,
    val meetingId: Long = 0,
    val title: String = "",
    val description: String? = null,
    val assignedTo: Long = 0,
    val assignedBy: Long? = null,
    val priority: String = "MEDIUM",
    val dueDate: String? = null,
    val state: String = "PENDING_CONFIRM",
    val completionNote: String? = null,
    val isAiExtracted: Boolean = true,
    val isVisible: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val completedAt: String? = null
)

@Serializable
data class MemoDto(
    val id: Long = 0,
    val userId: Long = 0,
    val title: String = "",
    val content: String? = null,
    val priority: String = "NORMAL",
    val reminderAt: String? = null,
    val isCompleted: Boolean = false,
    val color: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class NotificationDto(
    val id: Long = 0,
    val userId: Long = 0,
    val type: String = "",
    val title: String = "",
    val body: String? = null,
    val referenceType: String? = null,
    val referenceId: Long? = null,
    val isRead: Boolean = false,
    val createdAt: String? = null
)

@Serializable
data class FileDto(
    val id: Long = 0,
    val fileName: String = "",
    val fileSize: Long = 0,
    val mimeType: String = "",
    val ossKey: String = "",
    val fileType: String = "ATTACHMENT",
    val uploadedBy: Long = 0,
    val createdAt: String? = null
)

@Serializable
data class UserDto(
    val id: Long = 0,
    val username: String = "",
    val displayName: String = "",
    val phone: String = "",
    val email: String? = null,
    val departmentId: Long? = null,
    val title: String? = null,
    val isActive: Boolean = true,
    val roles: List<RoleDto> = emptyList()
)

@Serializable
data class RoleDto(
    val id: Int = 0,
    val code: String = "",
    val name: String = ""
)
