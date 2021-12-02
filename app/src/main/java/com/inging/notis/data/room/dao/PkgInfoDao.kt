package com.inging.notis.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.inging.notis.data.room.entity.PkgInfo
import kotlinx.coroutines.flow.Flow

/**
 * Created by annasu on 2021/04/27.
 */
@Dao
interface PkgInfoDao : BaseDao<PkgInfo> {

    @Query("select * from PkgInfo")
    suspend fun getAll(): List<PkgInfo>

    @Query("select * from PkgInfo")
    fun getAllFlow(): Flow<List<PkgInfo>>

    @Query("select * from PkgInfo where pkgName = :pkgName")
    fun findByPkgName(pkgName: String): PkgInfo?

    @Query("update PkgInfo set isBlock = :enable where pkgName = :pkgName")
    fun updateBlock(pkgName: String, enable: Boolean)

    @Query("update PkgInfo set isSave = :enable where pkgName = :pkgName")
    fun updateSave(pkgName: String, enable: Boolean)
}