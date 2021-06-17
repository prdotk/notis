package com.inging.notis.ui.main.msg

import androidx.core.app.NotificationCompat.CATEGORY_MESSAGE
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.inging.notis.data.model.SimpleSummaryData
import com.inging.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MsgListViewModel @Inject constructor(
    private val repository: NotiRepository
) : ViewModel() {

    // 메시지 편집모드
    var isEditMode = ObservableBoolean(false)

    val messageList = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        repository.getSummaryListByCategory(CATEGORY_MESSAGE)
    }.flow
        .cachedIn(viewModelScope)

    val deleteList = ObservableArrayList<SimpleSummaryData>()

    fun selectTotalNoti() {
        viewModelScope.launch(Dispatchers.IO) {
            deleteList.clear()
            repository.getSummaryIdListByCategory(CATEGORY_MESSAGE).forEach { notiId ->
                deleteList.add(notiId)
            }
        }
    }

    fun clearDeleteList() {
        deleteList.clear()
    }

    suspend fun delete() {
        withContext(Dispatchers.IO) {
            deleteList.forEach { data ->
                repository.deleteSummaryAndNoti(data.pkgName, data.summaryText)
            }
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            repository.deleteAllMsg()
        }
    }

    suspend fun readAll() {
        withContext(Dispatchers.IO) {
            repository.readAllMsg()
        }
    }
}