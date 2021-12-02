package com.inging.notis.data.room.entity

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

    // undo 구현위해 사용
    var summaryDeleted: Boolean = false

) : IgnoreInfo()
