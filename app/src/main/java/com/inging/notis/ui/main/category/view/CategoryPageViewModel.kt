package com.inging.notis.ui.main.category.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.inging.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoryPageViewModel @Inject constructor(
    private val notiRepository: NotiRepository
) : ViewModel() {

    fun getSummaryList(category: String) = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        notiRepository.getSummaryListByCategory(category)
    }.flow
        .cachedIn(viewModelScope)
}