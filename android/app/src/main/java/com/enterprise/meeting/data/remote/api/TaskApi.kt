package com.enterprise.meeting.data.remote.api

import com.enterprise.meeting.data.remote.dto.*
import retrofit2.http.*

interface TaskApi {
    @GET("tasks")
    suspend fun listTasks(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("userId") userId: Long? = null
    ): ApiResponseDto<List<TaskDto>>

    @GET("tasks/{id}")
    suspend fun getTask(@Path("id") taskId: Long): ApiResponseDto<TaskDto>

    @POST("tasks/{id}/confirm")
    suspend fun confirmTask(@Path("id") taskId: Long): ApiResponseDto<TaskDto>

    @POST("tasks/{id}/progress")
    suspend fun updateProgress(
        @Path("id") taskId: Long,
        @Body request: ProgressRequest
    ): ApiResponseDto<TaskDto>

    @POST("tasks/{id}/complete")
    suspend fun completeTask(
        @Path("id") taskId: Long,
        @Body request: CompleteRequest?
    ): ApiResponseDto<TaskDto>

    @POST("tasks/{id}/reject")
    suspend fun rejectTask(
        @Path("id") taskId: Long,
        @Body request: RejectRequest?
    ): ApiResponseDto<TaskDto>
}

data class ProgressRequest(val note: String? = null)
data class CompleteRequest(val note: String? = null)
data class RejectRequest(val reason: String? = null)
