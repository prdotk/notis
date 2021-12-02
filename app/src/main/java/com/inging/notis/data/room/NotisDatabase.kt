package com.inging.notis.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.inging.notis.data.room.dao.*
import com.inging.notis.data.room.entity.*

/**
 * Created by annasu on 2021/04/26.
 * vesion 2: add pending intent + remote input
 */
@Database(
    entities = [
        NotiInfo::class,
        SummaryInfo::class,
        PkgNotiInfo::class,
        SearchHistoryInfo::class,
        PkgInfo::class
    ],
    version = 6,
    exportSchema = false
)
abstract class NotisDatabase : RoomDatabase() {
    abstract fun getNotiInfoDao(): NotiInfoDao
    abstract fun getSummaryInfoDao(): SummaryInfoDao
    abstract fun getPkgNotiInfoDao(): PkgNotiInfoDao
    abstract fun getSearchHistoryDao(): SearchHistoryDao
    abstract fun getPkgInfoDao(): PkgInfoDao
}