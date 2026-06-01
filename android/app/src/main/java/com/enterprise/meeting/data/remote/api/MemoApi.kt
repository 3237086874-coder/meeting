package com.enterprise.meeting.data.remote.api

import com.enterprise.meeting.data.remote.dto.*
import retrofit2.http.*

interface MemoApi {
    @GET("memos")
    suspend fun listMemos(
        @Query("userId") userId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponseDto<List<MemoDto>>

    @GET("memos/{id}")
    suspend fun getMemo(@Path("id") memoId: Long): ApiResponseDto<MemoDto>

    @POST("memos")
    suspend fun createMemo(@Body request: CreateMemoRequestDto): ApiResponseDto<MemoDto>

    @PUT("memos/{id}")
    suspend fun updateMemo(
        @Path("id") memoId: Long,
        @Body request: UpdateMemoRequestDto
    ): ApiResponseDto<MemoDto>

    @DELETE("memos/{id}")
    suspend fun deleteMemo(@Path("id") memoId: Long): ApiResponseDto<Unit>
}

data class CreateMemoRequestDto(
    val userId: Long,
    val title: String,
    val content: String? = null,
    val priority: String = "NORMAL",
    val reminderAt: String? = null,
    val color: String? = null
)

data class UpdateMemoRequestDto(
    val title: String? = null,
    val content: String? = null,
    val priority: String? = null,
    val reminderAt: String? = null,
    val isCompleted: Boolean? = null,
    val color: String? = null
)
