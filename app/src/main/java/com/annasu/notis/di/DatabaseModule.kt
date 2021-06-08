package com.annasu.notis.di

import android.content.Context
import androidx.room.Room
import com.annasu.notis.data.room.NotisDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by annasu on 2021/04/26.
 */
@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNotisDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        NotisDatabase::class.java,
        "nm_db"
    ).build()

    @Provides
    fun provideNotiInfo(db: NotisDatabase) = db.getNotiInfoDao()

    @Provides
    fun provideSummaryInfo(db: NotisDatabase) = db.getSummaryInfoDao()

    @Provides
    fun providePkgNotiInfo(db: NotisDatabase) = db.getPkgNotiInfoDao()
}