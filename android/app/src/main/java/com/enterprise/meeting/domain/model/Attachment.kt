package com.enterprise.meeting.domain.model

data class Attachment(
    val id: Long,
    val taskId: Long,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val url: String,
    val thumbnailUrl: String?,
    val uploadedBy: Long,
    val createdAt: Long
)
