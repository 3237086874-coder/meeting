package com.enterprise.meeting.data.remote.api

import com.enterprise.meeting.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponseDto<LoginResponseDto>

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequest): ApiResponseDto<LoginResponseDto>
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)
