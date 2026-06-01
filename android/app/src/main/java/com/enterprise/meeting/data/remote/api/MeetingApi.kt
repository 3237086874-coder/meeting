package com.enterprise.meeting.data.remote.api

import com.enterprise.meeting.data.remote.dto.*
import retrofit2.http.*

interface MeetingApi {
    @POST("meetings")
    suspend fun createMeeting(@Body request: CreateMeetingRequest): ApiResponseDto<MeetingDto>

    @GET("meetings/{id}")
    suspend fun getMeeting(@Path("id") meetingId: Long): ApiResponseDto<MeetingDto>

    @GET("meetings")
    suspend fun listMeetings(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("userId") userId: Long? = null
    ): ApiResponseDto<List<MeetingDto>>

    @POST("meetings/{id}/review")
    suspend fun reviewMeeting(
        @Path("id") meetingId: Long,
        @Body request: ReviewRequest
    ): ApiResponseDto<MeetingDto>

    @POST("meetings/{id}/publish")
    suspend fun publishMeeting(@Path("id") meetingId: Long): ApiResponseDto<MeetingDto>

    @POST("meetings/{id}/archive")
    suspend fun archiveMeeting(@Path("id") meetingId: Long): ApiResponseDto<MeetingDto>
}

data class CreateMeetingRequest(
    val title: String,
    val description: String? = null,
    val meetingTime: String? = null,
    val location: String? = null,
    val durationMinutes: Int? = null,
    val createdBy: Long,
    val assignedReviewer: Long? = null
)

data class ReviewRequest(
    val approved: Boolean,
    val comment: String? = null
)
