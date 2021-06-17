package com.inging.notis.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.inging.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: NotiRepository
) : ViewModel() {

    // 검색어
    var word = ""

    // 노티 검색
    fun searchNotiInfoList(word: String) = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        repository.searchNotiInfoList(word)
    }.flow
        .cachedIn(viewModelScope)
}