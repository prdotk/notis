package com.inging.notis.di

import android.content.Context
import androidx.room.Room
import com.inging.notis.data.room.*
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
    ).addMigrations(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4,
        MIGRATION_4_5,
        MIGRATION_5_6
    ).build()

    @Provides
    fun provideNotiInfo(db: NotisDatabase) = db.getNotiInfoDao()

    @Provides
    fun provideSummaryInfo(db: NotisDatabase) = db.getSummaryInfoDao()

    @Provides
    fun providePkgNotiInfo(db: NotisDatabase) = db.getPkgNotiInfoDao()

    @Provides
    fun provideSearchHistoryInfo(db: NotisDatabase) = db.getSearchHistoryDao()

    @Provides
    fun providePkgInfo(db: NotisDatabase) = db.getPkgInfoDao()
}