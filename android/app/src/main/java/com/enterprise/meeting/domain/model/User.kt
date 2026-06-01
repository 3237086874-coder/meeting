package com.enterprise.meeting.domain.model

data class User(
    val id: Long,
    val username: String,
    val displayName: String,
    val phone: String,
    val email: String?,
    val departmentId: Long?,
    val departmentName: String?,
    val roles: List<String>,
    val isActive: Boolean
)
