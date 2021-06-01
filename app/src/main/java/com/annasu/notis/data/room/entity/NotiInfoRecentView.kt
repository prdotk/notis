package com.annasu.notis.data.room.entity

import androidx.room.DatabaseView
import androidx.room.Embedded

/**
 * Created by datasaver on 2021/04/27.
 */
@DatabaseView("""select n.* from NotiInfo n join PkgInfo p
    on n.pkgName = p.pkgName and n.pkgNotiId > p.lastPkgNotiId - 4""")
data class NotiInfoRecentView(

    @Embedded
    val notiInfo: NotiInfo
)
