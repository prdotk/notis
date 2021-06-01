package com.annasu.notis.data.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Created by datasaver on 2021/05/12.
 */

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table NotiInfo add column senderType INTEGER not null default 0")
        database.execSQL("alter table SummaryInfo add column senderType INTEGER not null default 0")
    }
}