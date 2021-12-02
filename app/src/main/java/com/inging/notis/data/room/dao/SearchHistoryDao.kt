package com.inging.notis.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.inging.notis.data.room.entity.SearchHistoryInfo

/**
 * Created by annasu on 2021/04/26.
 */
@Dao
interface SearchHistoryDao : BaseDao<SearchHistoryInfo> {

    @Query("select * from SearchHistoryInfo order by timestamp desc")
    fun getAll(): PagingSource<Int, SearchHistoryInfo>

    @Query("select * from SearchHistoryInfo where word = :word")
    fun findByWord(word: String): SearchHistoryInfo?

    @Query("delete from SearchHistoryInfo where word in (:word)")
    suspend fun deleteSearchHistoryInfoByWord(word: String)
}