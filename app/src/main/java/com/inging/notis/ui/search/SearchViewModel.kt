package com.inging.notis.ui.search

import androidx.core.app.NotificationCompat.CATEGORY_MESSAGE
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.inging.notis.constant.NotiViewType
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: NotiRepository
) : ViewModel() {

    // 검색어
    var word = ""

    // 히스토리에서 선택한 검색어
    var wordHistory = MutableLiveData<String>()

    // 노티 검색
    fun searchNotiInfoList(word: String) = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        repository.searchNotiInfoList(word)
    }.flow
        .cachedIn(viewModelScope)

    var deleteInfo = NotiInfo(-1, senderType = NotiViewType.LEFT)

    // 임시 삭제
    suspend fun undoDelete() {
        withContext(Dispatchers.IO) {
            repository.updateNotiDeleted(listOf(deleteInfo.notiId), true)
        }
    }

    // 임시 삭제 복구
    suspend fun undoRestore() {
        withContext(Dispatchers.IO) {
            repository.updateNotiDeleted(listOf(deleteInfo.notiId), false)
        }
    }

    // 삭제
    fun delete(info: NotiInfo) {
        CoroutineScope(Dispatchers.IO).launch {
            when (info.category) {
                CATEGORY_MESSAGE ->
                    repository.deleteMsgNotiListAndUpdateSummary(
                        info.notiId,
                        info.pkgName,
                        info.summaryText
                    )
                else ->
                    repository.deleteNotiAndUpdatePkgNoti(info)
            }
        }
    }

    // 검색 히스토리 목록
    val searchHistoryList = Pager(
        config = PagingConfig(pageSize = 30)
    ) {
        repository.getSearchHistoryList()
    }.flow
        .cachedIn(viewModelScope)

    // 검색 히스토리 저장
    fun saveSearchHistory(word: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.saveSearchHistory(word)
        }
    }

    // 검색 히스토리 삭제
    fun deleteSearchHistory(word: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteSearchHistory(word)
        }
    }
}