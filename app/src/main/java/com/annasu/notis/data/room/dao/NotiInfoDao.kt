package com.annasu.notis.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.annasu.notis.data.room.entity.NotiInfo
import kotlinx.coroutines.flow.Flow

/**
 * Created by datasaver on 2021/04/26.
 */
@Dao
interface NotiInfoDao : BaseDao<NotiInfo> {

    @Query("select * from NotiInfo")
    fun getAll(): PagingSource<Int, NotiInfo>

    // 최신 노티
    @Query("select * from NotiInfo where pkgName = :pkgName and summaryText = :summaryText order by timestamp desc limit 1")
    fun getRecentNotiInfoByPkgNameAndSummaryText(pkgName: String, summaryText: String): Flow<NotiInfo?>

    // 중복 노티
    @Query("""select * from NotiInfo where 
        pkgName = :pkgName and summaryText = :summaryText and text = :text 
        order by timestamp desc limit 1""")
    suspend fun getRecentNotiInfoByPkgNameAndSummaryTextAndText(pkgName: String, summaryText: String, text: String): NotiInfo?

    // 노티 목록
    @Query("select * from NotiInfo where pkgName = :pkgName and summaryText = :summaryText order by timestamp desc")
    fun getNotiInfoListByPkgNameAndSummaryText(pkgName: String, summaryText: String): PagingSource<Int, NotiInfo>

    // 노티 ID 목록
    @Query("select notiId from NotiInfo where pkgName = :pkgName and summaryText = :summaryText order by timestamp desc")
    suspend fun getNotiIdListByPkgNameAndSummaryText(pkgName: String, summaryText: String): List<Long>

    // 오래된 마지막 노티
    @Query("select notiId from NotiInfo where pkgName = :pkgName and summaryText = :summaryText order by timestamp limit 1")
    suspend fun getLastNotiId(pkgName: String, summaryText: String): Long

    // 검색
    @Query("""select * from NotiInfo where 
        summaryText like '%' || :word || '%'
        or title like '%' || :word || '%'
        or text like '%' || :word || '%' order by timestamp desc""")
    fun searchNotiInfoList(word: String): PagingSource<Int, NotiInfo>

    // 검색 (escape)
    @Query("""select * from NotiInfo where 
        summaryText like '%' || :word || '%' escape '\'
        or title like '%' || :word || '%' escape '\'
        or text like '%' || :word || '%' escape '\' order by timestamp desc""")
    fun searchNotiInfoListEscape(word: String): PagingSource<Int, NotiInfo>

    // 검색, 노티 ID 리스트
    @Query("""select notiId from NotiInfo where 
        summaryText like '%' || :word || '%'
        or title like '%' || :word || '%'
        or text like '%' || :word || '%' order by timestamp desc""")
    suspend fun searchNotiIdList(word: String): List<Long>

    // 검색 (escape), 노티 ID 리스트
    @Query("""select notiId from NotiInfo where 
        summaryText like '%' || :word || '%' escape '\'
        or title like '%' || :word || '%' escape '\'
        or text like '%' || :word || '%' escape '\' order by timestamp desc""")
    suspend fun searchNotiIdListEscape(word: String): List<Long>

    @Query("delete from NotiInfo where pkgName in (:pkgName) and summaryText in (:summaryText)")
    suspend fun deleteNotiInfoByPkgNameAndSummaryText(pkgName: String, summaryText: String)

    @Query("delete from NotiInfo where notiId in (:idList)")
    suspend fun deleteNotiInfoByIdList(idList: List<Long>)
}