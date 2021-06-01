package com.annasu.notis.ui.main.pkg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.annasu.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PkgViewModel @Inject constructor(
    notiRepository: NotiRepository
) : ViewModel() {

    val pkgInfoWithNotiRecentViews = Pager(
        config = PagingConfig(pageSize = 10)
    ) {
        notiRepository.getPkgInfoWithNotiRecentViews()
    }.flow
        .cachedIn(viewModelScope)
}