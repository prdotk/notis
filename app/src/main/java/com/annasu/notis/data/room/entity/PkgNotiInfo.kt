package com.annasu.notis.data.room.entity

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
    val lastPkgNoti: NotiInfo,

    // 패키지 노티 갯수
    var notiCount: Long
)
