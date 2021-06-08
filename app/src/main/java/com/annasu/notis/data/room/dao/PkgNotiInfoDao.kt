package com.annasu.notis.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.annasu.notis.data.room.entity.PkgNotiInfo

/**
 * Created by annasu on 2021/04/27.
 */
@Dao
interface PkgNotiInfoDao : BaseDao<PkgNotiInfo> {

    @Query("select * from PkgNotiInfo order by timestamp desc")
    fun getAll(): PagingSource<Int, PkgNotiInfo>

    // 패키지명, 서머리 텍스트로 정보 가져오기
    @Query("select * from PkgNotiInfo where pkgNameId = :pkgName")
    fun getPkgNotiInfoByPkgName(pkgName: String): PkgNotiInfo?
}