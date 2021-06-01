package com.annasu.notis.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.annasu.notis.data.room.entity.PkgInfo
import com.annasu.notis.data.room.entity.PkgInfoWithNotiInfo

/**
 * Created by datasaver on 2021/04/27.
 */
@Dao
interface PkgInfoDao : BaseDao<PkgInfo> {

    @Query("select * from PkgInfo where pkgName = :pkgName")
    fun getPkgInfo(pkgName: String): PkgInfo?

    @Transaction
    @Query("select * from PkgInfo order by updateTime desc")
    fun getPkgInfoWithNotiInfoList(): PagingSource<Int, PkgInfoWithNotiInfo>
}