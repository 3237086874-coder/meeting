package com.enterprise.meeting.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "pending_actions")
data class PendingActionEntity(
    @PrimaryKey val id: Long = 0,
    @ColumnInfo(name = "action_type") val actionType: String,
    val payload: String,
    @ColumnInfo(name = "idempotency_key") val idempotencyKey: String = UUID.randomUUID().toString(),
    val status: String = "PENDING",
    @ColumnInfo(name = "retry_count") val retryCount: Int = 0,
    @ColumnInfo(name = "last_error") val lastError: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
