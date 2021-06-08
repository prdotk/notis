package com.annasu.notis.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.annasu.notis.data.room.dao.NotiInfoDao
import com.annasu.notis.data.room.dao.PkgNotiInfoDao
import com.annasu.notis.data.room.dao.SummaryInfoDao
import com.annasu.notis.data.room.entity.NotiInfo
import com.annasu.notis.data.room.entity.PkgNotiInfo
import com.annasu.notis.data.room.entity.SummaryInfo

/**
 * Created by annasu on 2021/04/26.
 * vesion 2: add pending intent + remote input
 */
@Database(
    entities = [
        NotiInfo::class,
        SummaryInfo::class,
        PkgNotiInfo::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NotisDatabase : RoomDatabase() {
    abstract fun getNotiInfoDao(): NotiInfoDao
    abstract fun getSummaryInfoDao(): SummaryInfoDao
    abstract fun getPkgNotiInfoDao(): PkgNotiInfoDao
}