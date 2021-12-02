package com.inging.notis.data.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by annasu on 2021/04/27.
 */
// 메시지 제외한 노티 패키지 정보 저장
@Entity
data class PkgNotiInfo(

    @PrimaryKey(autoGenerate = false)
    val pkgNameId: String = "",

    @Embedded
    var recentNotiInfo: NotiInfo,

    // 패키지 노티 갯수
    var notiCount: Long,

    // 안읽은 메시지 갯수
    var unreadCnt: Int,

    // undo 구현위해 사용
    var pkgNotiDeleted: Boolean = false
)
