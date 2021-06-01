package com.annasu.notis.data.room.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Created by datasaver on 2021/04/27.
 */
data class PkgInfoWithNotiInfo(

    @Embedded
    val pkgInfo: PkgInfo,

    @Relation(
        parentColumn = "pkgName",
        entityColumn = "pkgName"
    )
    val notiInfoList: List<NotiInfoRecentView>
)
