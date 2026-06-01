package com.enterprise.meeting.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.enterprise.meeting.data.local.dao.MeetingDao
import com.enterprise.meeting.data.local.dao.MemoDao
import com.enterprise.meeting.data.local.dao.PendingActionDao
import com.enterprise.meeting.data.local.dao.TaskDao
import com.enterprise.meeting.data.local.entity.MeetingEntity
import com.enterprise.meeting.data.local.entity.MemoEntity
import com.enterprise.meeting.data.local.entity.PendingActionEntity
import com.enterprise.meeting.data.local.entity.TaskEntity

@Database(
    entities = [
        MeetingEntity::class,
        TaskEntity::class,
        MemoEntity::class,
        PendingActionEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun meetingDao(): MeetingDao
    abstract fun taskDao(): TaskDao
    abstract fun memoDao(): MemoDao
    abstract fun pendingActionDao(): PendingActionDao
}
