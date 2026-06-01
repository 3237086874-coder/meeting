package com.enterprise.meeting.data.remote.api

import com.enterprise.meeting.data.remote.dto.*
import retrofit2.http.*

interface NotificationApi {
    @GET("notifications")
    suspend fun listNotifications(
        @Query("userId") userId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponseDto<List<NotificationDto>>

    @GET("notifications/unread-count")
    suspend fun getUnreadCount(@Query("userId") userId: Long): ApiResponseDto<Map<String, Int>>

    @PATCH("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") notificationId: Long): ApiResponseDto<Unit>

    @POST("notifications/read-all")
    suspend fun markAllAsRead(@Query("userId") userId: Long): ApiResponseDto<Unit>
}
