package com.annasu.notis.data.room.entity

import androidx.room.Embedded
import androidx.room.Entity

/**
 * Created by annasu on 2021/04/27.
 */
// 메시지 정보 저장
@Entity(
    primaryKeys = ["pkgName", "summaryText"],
    ignoredColumns = ["isChecked"])
data class SummaryInfo(

    var unreadCnt: Int,

    @Embedded
    var recentNotiInfo: NotiInfo,

) : IgnoreInfo()
