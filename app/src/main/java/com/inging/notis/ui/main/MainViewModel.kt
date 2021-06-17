package com.inging.notis.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.inging.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by annasu on 2021/05/21.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    repository: NotiRepository
) : ViewModel() {

    // 전체 안읽은 갯수
    val totalUnreadCount = repository.getTotalUnreadCount().asLiveData()
}