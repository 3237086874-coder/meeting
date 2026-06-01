package com.enterprise.meeting.domain.repository

import com.enterprise.meeting.domain.model.User

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoginResult>
    suspend fun refreshToken(): Result<LoginResult>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    fun isLoggedIn(): Boolean
}

data class LoginResult(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)
