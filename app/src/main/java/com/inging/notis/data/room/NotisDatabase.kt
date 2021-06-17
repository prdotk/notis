package com.inging.notis.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.inging.notis.data.room.dao.NotiInfoDao
import com.inging.notis.data.room.dao.PkgNotiInfoDao
import com.inging.notis.data.room.dao.SummaryInfoDao
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.data.room.entity.PkgNotiInfo
import com.inging.notis.data.room.entity.SummaryInfo

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