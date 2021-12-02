package com.inging.notis.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.inging.notis.data.room.entity.NotiInfo
import kotlinx.coroutines.flow.Flow

/**
 * Created by annasu on 2021/04/26.
 */
@Dao
interface NotiInfoDao : BaseDao<NotiInfo> {

    @Query("select * from NotiInfo where deleted = 0")
    fun getAll(): PagingSource<Int, NotiInfo>

    // 최신 노티 Flow
    @Query("select * from NotiInfo where pkgName = :pkgName and summaryText = :summaryText and deleted = 0 order by timestamp desc limit 1")
    fun getRecentNotiInfoByPkgNameAndSummaryTextFlow(
        pkgName: String,
        summaryText: String
    ): Flow<NotiInfo?>

    // 최신 노티 (패키지, 서머리)
    @Query("select * from NotiInfo where pkgName = :pkgName and summaryText = :summaryText and deleted = 0 order by timestamp desc limit 1")
    fun getRecentNotiInfoByPkgNameAndSummaryText(pkgName: String, summaryText: String): NotiInfo?

    // 최신 노티 (패키지, 메시지 아닌)
    @Query("select * from NotiInfo where pkgName = :pkgName and category != 'msg' and deleted = 0 order by timestamp desc limit 1")
    fun getRecentNotiInfoByPkgNameAndNotMsg(pkgName: String): NotiInfo?

    // 패키지 별 노티 갯수 (메시지 아닌)
    @Query("select count(*) from NotiInfo where pkgName = :pkgName and deleted = 0 and category != 'msg'")
    fun getPkgNotiCountAndNotMsg(pkgName: String): Long

    // 중복 노티
    @Query(
        """select * from NotiInfo where 
        pkgName = :pkgName and summaryText = :summaryText and text = :text 
        order by timestamp desc limit 1"""
    )
    suspend fun getRecentNotiInfoByPkgNameAndSummaryTextAndText(
        pkgName: String,
        summaryText: String,
        text: String
    ): NotiInfo?

    // 노티 목록 (패키지 별)
    @Query("select * from NotiInfo where pkgName = :pkgName and category != 'msg' and deleted = 0 order by timestamp desc")
    fun getNotiInfoListByPkgNameNotMsg(pkgName: String): PagingSource<Int, NotiInfo>

    // 노티 목록 (패키지 / 서머리 텍스트 별)
    @Query("select * from NotiInfo where pkgName = :pkgName and summaryText = :summaryText and deleted = 0 order by timestamp desc")
    fun getNotiInfoListByPkgNameAndSummaryText(
        pkgName: String,
        summaryText: String
    ): PagingSource<Int, NotiInfo>

    // 노티 갯수 (패키지 / 서머리 텍스트 별)
    @Query("select count(*) from NotiInfo where pkgName = :pkgName and summaryText = :summaryText and deleted = 0")
    fun getNotiCntByPkgNameAndSummaryText(
        pkgName: String,
        summaryText: String
    ): Long

    // 노티 갯수 (패키지 / 메시지 아닌)
    @Query("select count(*) from NotiInfo where pkgName = :pkgName and category != 'msg' and deleted = 0")
    fun getNotiCntByPkgNameAndNotMsg(pkgName: String): Long

    // 노티 목록 (메시지 제외 전체)
    @Query("select * from NotiInfo where category != 'msg' and deleted = 0 order by timestamp desc")
    fun getNotiInfoListByNotMsg(): PagingSource<Int, NotiInfo>

    // 노티 ID 목록 (패키지, 서머리)
    @Query("select notiId from NotiInfo where pkgName = :pkgName and summaryText = :summaryText and deleted = 0 order by timestamp desc")
    suspend fun getNotiIdListByPkgNameAndSummaryText(
        pkgName: String,
        summaryText: String
    ): List<Long>

    // 노티 ID 목록 (패키지)
    @Query("select notiId from NotiInfo where pkgName = :pkgName and deleted = 0 order by timestamp desc")
    suspend fun getNotiIdListByPkgName(
        pkgName: String
    ): List<Long>

    // 오래된 마지막 노티
    @Query("select notiId from NotiInfo where pkgName = :pkgName and summaryText = :summaryText and deleted = 0 order by timestamp limit 1")
    suspend fun getLastNotiId(pkgName: String, summaryText: String): Long

    // 검색
    @Query(
        """select * from NotiInfo where 
        (summaryText like '%' || :word || '%'
        or title like '%' || :word || '%'
        or text like '%' || :word || '%') 
        and deleted = 0 order by timestamp desc"""
    )
    fun searchNotiInfoList(word: String): PagingSource<Int, NotiInfo>

    // 검색 (escape)
    @Query(
        """select * from NotiInfo where 
        (summaryText like '%' || :word || '%' escape '\'
        or title like '%' || :word || '%' escape '\'
        or text like '%' || :word || '%' escape '\')
        and deleted = 0 order by timestamp desc"""
    )
    fun searchNotiInfoListEscape(word: String): PagingSource<Int, NotiInfo>

    // 검색, 노티 ID 리스트
    @Query(
        """select notiId from NotiInfo where 
        (summaryText like '%' || :word || '%'
        or title like '%' || :word || '%'
        or text like '%' || :word || '%') 
        and deleted = 0 order by timestamp desc"""
    )
    suspend fun searchNotiIdList(word: String): List<Long>

    // 검색 (escape), 노티 ID 리스트
    @Query(
        """select notiId from NotiInfo where 
        (summaryText like '%' || :word || '%' escape '\'
        or title like '%' || :word || '%' escape '\'
        or text like '%' || :word || '%' escape '\') 
        and deleted = 0 order by timestamp desc"""
    )
    suspend fun searchNotiIdListEscape(word: String): List<Long>

    // 노티 임시 삭제 업데이트
    @Query("update NotiInfo set deleted = :deleted where notiId in (:idList)")
    suspend fun updateDeleted(idList: List<Long>, deleted: Boolean)

    @Query("delete from NotiInfo where pkgName in (:pkgName) and summaryText in (:summaryText)")
    suspend fun deleteNotiInfoByPkgNameAndSummaryText(pkgName: String, summaryText: String)

    @Query("delete from NotiInfo where pkgName in (:pkgName)")
    suspend fun deleteNotiInfoByPkgName(pkgName: String)

    @Query("delete from NotiInfo where notiId in (:idList)")
    suspend fun deleteNotiInfoByIdList(idList: List<Long>)

    @Query("delete from NotiInfo where notiId in (:idList)")
    suspend fun deleteNotiInfoById(idList: Long)

    // 메시지 노티 전부 삭제
    @Query("delete from NotiInfo where category == 'msg'")
    suspend fun deleteAllMsg()

    // 메시지 아닌 노티 전부 삭제
    @Query("delete from NotiInfo where category != 'msg'")
    suspend fun deleteAllNotMsg()

    // 메시지 ID 로 삭제
    @Query("delete from NotiInfo where notiId = :notiId")
    suspend fun deleteByNotiId(notiId: Long)
}