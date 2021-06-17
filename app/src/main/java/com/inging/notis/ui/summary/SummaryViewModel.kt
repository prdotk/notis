package com.inging.notis.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.inging.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val notiRepository: NotiRepository
) : ViewModel() {

    var pkgName = ""

    fun getTitleList(pkgName: String) = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        notiRepository.getSummaryListByPkgName(pkgName)
    }.flow
        .cachedIn(viewModelScope)
}