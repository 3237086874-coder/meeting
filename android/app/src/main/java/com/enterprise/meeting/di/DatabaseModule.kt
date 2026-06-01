package com.enterprise.meeting.di

import android.content.Context
import androidx.room.Room
import com.enterprise.meeting.data.local.AppDatabase
import com.enterprise.meeting.data.local.dao.MeetingDao
import com.enterprise.meeting.data.local.dao.MemoDao
import com.enterprise.meeting.data.local.dao.PendingActionDao
import com.enterprise.meeting.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "meeting_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMeetingDao(db: AppDatabase): MeetingDao = db.meetingDao()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideMemoDao(db: AppDatabase): MemoDao = db.memoDao()

    @Provides
    fun providePendingActionDao(db: AppDatabase): PendingActionDao = db.pendingActionDao()
}
