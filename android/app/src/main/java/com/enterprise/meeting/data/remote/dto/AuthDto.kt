package com.enterprise.meeting.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val accessToken: String = "",
    val refreshToken: String = "",
    val userId: Long = 0,
    val username: String = "",
    val displayName: String = "",
    val roles: List<String> = emptyList()
)
