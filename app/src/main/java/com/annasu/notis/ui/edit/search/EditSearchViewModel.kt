package com.annasu.notis.ui.edit.search

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.annasu.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditSearchViewModel @Inject constructor(
    private val repository: NotiRepository
) : ViewModel() {

    var word = ""

    // 노티 검색
    var searchNotiInfoList = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        repository.searchNotiInfoList(word)
    }.flow
        .cachedIn(viewModelScope)

    val removeList = ObservableArrayList<Long>()

    fun selectTotalNoti() {
        viewModelScope.launch(Dispatchers.IO) {
            removeList.clear()
            repository.searchNotiIdList(word).forEach { notiId ->
                removeList.add(notiId)
            }
        }
    }

    suspend fun remove() {
        withContext(Dispatchers.IO) {
            repository.removeNotiInfoByIdList(removeList)
        }
    }
}