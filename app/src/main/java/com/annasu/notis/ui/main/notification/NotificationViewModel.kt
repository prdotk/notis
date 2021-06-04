package com.annasu.notis.ui.main.notification

import androidx.core.app.NotificationCompat.CATEGORY_MESSAGE
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.annasu.notis.data.model.SimpleSummaryData
import com.annasu.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotiRepository
) : ViewModel() {

    val messageList = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        repository.getSummaryListByCategory(CATEGORY_MESSAGE)
    }.flow
        .cachedIn(viewModelScope)

    val removeList = ObservableArrayList<SimpleSummaryData>()

    fun selectTotalNoti() {
        viewModelScope.launch(Dispatchers.IO) {
            removeList.clear()
            repository.getSummaryIdListByCategory(CATEGORY_MESSAGE).forEach { notiId ->
                removeList.add(notiId)
            }
        }
    }

    fun clearRemoveList() {
        removeList.clear()
    }

    suspend fun remove() {
        withContext(Dispatchers.IO) {
            removeList.forEach { data ->
                repository.removeSummaryInfoAndNotiInfo(data.pkgName, data.summaryText)
            }
        }
    }
}