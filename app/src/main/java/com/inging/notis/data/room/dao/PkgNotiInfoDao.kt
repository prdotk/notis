package com.inging.notis.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.inging.notis.data.room.entity.PkgNotiInfo

/**
 * Created by annasu on 2021/04/27.
 */
@Dao
interface PkgNotiInfoDao : BaseDao<PkgNotiInfo> {

    @Query("select * from PkgNotiInfo where pkgNotiDeleted = 0 order by timestamp desc")
    fun getAll(): PagingSource<Int, PkgNotiInfo>

    // 패키지명, 서머리 텍스트로 정보 가져오기
    @Query("select * from PkgNotiInfo where pkgNameId = :pkgName and pkgNotiDeleted = 0")
    fun getPkgNotiInfoByPkgName(pkgName: String): PkgNotiInfo?

    // 임시 삭제 업데이트
    @Query("update PkgNotiInfo set pkgNotiDeleted = :deleted where pkgNameId = :pkgName")
    suspend fun updateDeleted(pkgName: String, deleted: Boolean)

    // 패키지명으로 삭제
    @Query("delete from PkgNotiInfo where pkgNameId = :pkgName")
    suspend fun deleteByPkgName(pkgName: String)

    // 모두 삭제
    @Query("delete from PkgNotiInfo")
    suspend fun deleteAll()
}