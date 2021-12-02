package com.inging.notis.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by annasu on 2021/04/27.
 */
// 패키지 정보 저장
@Entity
data class PkgInfo(

    @PrimaryKey(autoGenerate = false)
    val pkgName: String = "",

    // 블럭 여부, 기본 true
    var isBlock: Boolean = true,

    // 저장 여부, 기본 true
    var isSave: Boolean = true
)
