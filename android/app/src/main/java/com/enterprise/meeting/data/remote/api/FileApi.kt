package com.enterprise.meeting.data.remote.api

import com.enterprise.meeting.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface FileApi {
    @Multipart
    @POST("files/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("meetingId") meetingId: RequestBody?,
        @Part("taskId") taskId: RequestBody?,
        @Part("fileType") fileType: RequestBody,
        @Part("uploadedBy") uploadedBy: RequestBody
    ): ApiResponseDto<FileDto>

    @GET("files/meeting/{meetingId}")
    suspend fun getMeetingFiles(@Path("meetingId") meetingId: Long): ApiResponseDto<List<FileDto>>

    @GET("files/task/{taskId}")
    suspend fun getTaskFiles(@Path("taskId") taskId: Long): ApiResponseDto<List<FileDto>>
}
