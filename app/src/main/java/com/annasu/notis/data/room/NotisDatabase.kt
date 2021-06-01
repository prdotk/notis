package com.annasu.notis.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.annasu.notis.data.room.dao.NotiInfoDao
import com.annasu.notis.data.room.dao.PkgInfoDao
import com.annasu.notis.data.room.dao.SummaryInfoDao
import com.annasu.notis.data.room.entity.NotiInfo
import com.annasu.notis.data.room.entity.NotiInfoRecentView
import com.annasu.notis.data.room.entity.PkgInfo
import com.annasu.notis.data.room.entity.SummaryInfo

/**
 * Created by datasaver on 2021/04/26.
 * vesion 2: add pending intent + remote input
 */
@Database(
    entities = [
        NotiInfo::class,
        PkgInfo::class,
        SummaryInfo::class,
    ],
    views = [
        NotiInfoRecentView::class
    ],
    version = 2,
    exportSchema = false
)
abstract class NotisDatabase : RoomDatabase() {
    abstract fun getNotiInfoDao(): NotiInfoDao
    abstract fun getPkgInfoDao(): PkgInfoDao
    abstract fun getSummaryInfoDao(): SummaryInfoDao
}