package com.annasu.notis.ui.edit.summary

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.annasu.notis.data.model.SimpleSummaryData
import com.annasu.notis.repository.NotiRepository
import com.annasu.notis.ui.edit.summary.EditSummaryActivity.Companion.MODE_EDIT_CATEGORY
import com.annasu.notis.ui.edit.summary.EditSummaryActivity.Companion.MODE_EDIT_PACKAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditSummaryViewModel @Inject constructor(
    private val repository: NotiRepository
) : ViewModel() {

    var mode = MODE_EDIT_CATEGORY
    var category = ""
    var pkgName = ""

    val summaryList = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        when (mode) {
            MODE_EDIT_PACKAGE -> repository.getSummaryListByPkgName(pkgName)
            else -> repository.getSummaryListByCategory(category)
        }
    }.flow
        .cachedIn(viewModelScope)

    val removeList = ObservableArrayList<SimpleSummaryData>()

    fun selectTotalNoti() {
        viewModelScope.launch(Dispatchers.IO) {
            removeList.clear()
            when (mode) {
                MODE_EDIT_PACKAGE -> repository.getSummaryIdListByPkgName(pkgName)
                else -> repository.getSummaryIdListByCategory(category)
            }.forEach { notiId ->
                removeList.add(notiId)
            }
        }
    }

    suspend fun remove() {
        withContext(Dispatchers.IO) {
            removeList.forEach { data ->
                repository.removeSummaryInfoAndNotiInfo(data.pkgName, data.summaryText)
            }
        }
    }
}