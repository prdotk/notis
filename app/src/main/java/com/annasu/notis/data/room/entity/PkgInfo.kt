package com.annasu.notis.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by datasaver on 2021/04/27.
 */
@Entity
data class PkgInfo(

    @PrimaryKey(autoGenerate = false)
    val pkgName: String = "",

    val updateTime: Long,

    // 마지막 NotiInfo.pkgNotiId
    val lastPkgNotiId: Long,
)
