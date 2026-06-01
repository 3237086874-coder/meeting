package com.enterprise.meeting.domain.repository

import com.enterprise.meeting.domain.model.Task

interface TaskRepository {
    suspend fun getTasks(userId: Long): Result<List<Task>>
    suspend fun getTaskById(id: Long): Result<Task>
    suspend fun confirmTask(taskId: Long): Result<Task>
    suspend fun updateProgress(taskId: Long, note: String?): Result<Task>
    suspend fun completeTask(taskId: Long, note: String?): Result<Task>
    suspend fun rejectTask(taskId: Long, reason: String?): Result<Task>
}
