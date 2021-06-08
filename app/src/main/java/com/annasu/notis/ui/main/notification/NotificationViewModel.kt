package com.annasu.notis.ui.main.notification

import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.annasu.notis.constant.NotiListMode
import com.annasu.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotiRepository
) : ViewModel() {

    // 모든 노티
    val allNotiList = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        repository.getNotiListByNotMsg()
    }.flow
        .cachedIn(viewModelScope)

    // 패키지별 대표노티
    val pkgNotiList = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        repository.getPkgNotiList()
    }.flow
        .cachedIn(viewModelScope)

    val listMode = ObservableInt(NotiListMode.ALL)
}