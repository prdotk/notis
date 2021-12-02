package com.inging.notis.data.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Created by datasaver on 2021/05/12.
 */

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE SearchHistoryInfo(
                word TEXT PRIMARY KEY NOT NULL,
                timestamp INTEGER NOT NULL
            )
            """.trimMargin())
//        database.execSQL("alter table NotiInfo add column senderType INTEGER not null default 0")
//        database.execSQL("alter table SummaryInfo add column senderType INTEGER not null default 0")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE PkgInfo(
                pkgName TEXT PRIMARY KEY NOT NULL,
                isBlock INTEGER NOT NULL,
                isSave INTEGER NOT NULL
            )
            """.trimMargin())
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table SummaryInfo add column summaryDeleted INTEGER not null default 0")
        database.execSQL("alter table SummaryInfo add column deleted INTEGER not null default 0")
        database.execSQL("alter table PkgNotiInfo add column pkgNotiDeleted INTEGER not null default 0")
        database.execSQL("alter table PkgNotiInfo add column deleted INTEGER not null default 0")
        database.execSQL("alter table NotiInfo add column deleted INTEGER not null default 0")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE RecentPkgNotiInfo(
                pkgNameId TEXT PRIMARY KEY NOT NULL,
                timestamp INTEGER NOT NULL,
                unreadCnt INTEGER NOT NULL
            )
            """.trimMargin())
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            DROP TABLE RecentPkgNotiInfo
            """.trimMargin())
    }
}