package com.enterprise.meeting.domain.repository

import com.enterprise.meeting.domain.model.Meeting

interface MeetingRepository {
    suspend fun getMeetings(userId: Long): Result<List<Meeting>>
    suspend fun getMeetingById(id: Long): Result<Meeting>
    suspend fun createMeeting(
        title: String,
        description: String?,
        createdBy: Long,
        assignedReviewer: Long?
    ): Result<Meeting>
    suspend fun reviewMeeting(meetingId: Long, approved: Boolean): Result<Meeting>
    suspend fun publishMeeting(meetingId: Long): Result<Meeting>
    suspend fun archiveMeeting(meetingId: Long): Result<Meeting>
}
