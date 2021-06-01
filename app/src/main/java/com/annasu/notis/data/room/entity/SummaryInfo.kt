package com.annasu.notis.data.room.entity

import androidx.room.Embedded
import androidx.room.Entity

/**
 * Created by datasaver on 2021/04/27.
 */

@Entity(
    primaryKeys = ["pkgName", "summaryText"],
    ignoredColumns = ["isChecked"])
data class SummaryInfo(

    var unreadCnt: Int,

    @Embedded
    val recentNotiInfo: NotiInfo,

) : IgnoreInfo()