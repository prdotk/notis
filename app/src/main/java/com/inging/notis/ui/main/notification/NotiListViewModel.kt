package com.inging.notis.ui.main.notification

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.data.room.entity.PkgNotiInfo
import com.inging.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NotiListViewModel @Inject constructor(
    private val repository: NotiRepository
) : ViewModel() {

    // 메시지 편집모드
    var isEditMode = ObservableBoolean(false)

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

    val listMode = ObservableInt(-1)

    val deleteNotiList = ObservableArrayList<NotiInfo>()

    val deletePkgList = ObservableArrayList<PkgNotiInfo>()

    fun clearDeleteList() {
        deleteNotiList.clear()
        deletePkgList.clear()
    }

    suspend fun deleteNoti() {
        withContext(Dispatchers.IO) {
            deleteNotiList.forEach { notiInfo ->
                repository.deleteNotiAndUpdatePkgNoti(notiInfo)
            }
        }
    }

    suspend fun deletePkgNoti() {
        withContext(Dispatchers.IO) {
            deletePkgList.forEach { pkgNotiInfo ->
                repository.deletePkgNotiAndNotiInfo(pkgNotiInfo)
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllNotMsg()
        }
    }
}