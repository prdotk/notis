package com.inging.notis.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.inging.notis.data.model.SimpleSummaryData
import com.inging.notis.data.room.entity.SummaryInfo
import kotlinx.coroutines.flow.Flow

/**
 * Created by annasu on 2021/04/27.
 */
@Dao
interface SummaryInfoDao : BaseDao<SummaryInfo> {

    @Query("select * from SummaryInfo where summaryDeleted = 0 order by timestamp desc")
    fun getAll(): PagingSource<Int, SummaryInfo>

    // 카테고리별 서머리 목록
    @Query("select * from SummaryInfo where category = :category and summaryDeleted = 0 order by timestamp desc")
    fun getSummaryInfoListByCategory(category: String): PagingSource<Int, SummaryInfo>

    // 패키지별 서머리 목록
    @Query("select * from SummaryInfo where pkgName = :pkgName and summaryDeleted = 0 order by timestamp desc")
    fun getSummaryInfoListByPkgName(pkgName: String): PagingSource<Int, SummaryInfo>

    // 카테고리별 서머리 ID 목록
    @Query("select pkgName, summaryText from SummaryInfo where category = :category and summaryDeleted = 0 order by timestamp desc")
    suspend fun getSummaryIdListByCategory(category: String): List<SimpleSummaryData>

    // 패키지별 서머리 ID 목록
    @Query("select pkgName, summaryText from SummaryInfo where pkgName = :pkgName and summaryDeleted = 0 order by timestamp desc")
    suspend fun getSummaryIdListByPkgName(pkgName: String): List<SimpleSummaryData>

    // 패키지명, 서머리 텍스트로 서머리 인포 가져오기
    @Query("select * from SummaryInfo where pkgName = :pkgName and summaryText = :summaryText and summaryDeleted = 0")
    fun getSummaryInfoByPkgNameAndSummaryText(pkgName: String, summaryText: String): SummaryInfo?

    // 전체 안읽음 갯수
    @Query("select sum(unreadCnt) from SummaryInfo where summaryDeleted = 0")
    fun getTotalUnreadCount(): Flow<Int?>

    // 카테고리별 안읽음 갯수
    @Query("select sum(unreadCnt) from SummaryInfo where category = :category and summaryDeleted = 0")
    fun getSummaryUnreadCountByCategory(category: String): Flow<Int?>

    // 서머리 삭제
    @Query("delete from SummaryInfo where pkgName = :pkgName and summaryText = :summaryText")
    suspend fun deleteSummaryInfoByPkgNameAndSummaryText(pkgName: String, summaryText: String)

    // 서머리 임시 삭제 업데이트
    @Query("update SummaryInfo set summaryDeleted = :deleted where pkgName = :pkgName and summaryText = :summaryText")
    suspend fun updateDeleted(pkgName: String, summaryText: String, deleted: Boolean)

    // 서머리 모두 삭제
    @Query("delete from SummaryInfo")
    suspend fun deleteAll()

    // 메시지 모두 읽기
    @Query("update SummaryInfo set unreadCnt = 0 where unreadCnt != 0")
    suspend fun updateUnreadCntZero()
}