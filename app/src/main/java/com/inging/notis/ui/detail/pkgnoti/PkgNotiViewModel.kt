package com.inging.notis.ui.detail.pkgnoti

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PkgNotiViewModel @Inject constructor(
    private val repository: NotiRepository
) : ViewModel() {

    var pkgName = ""
    var word: String = ""
    var notiId: Long = -1

    // 편집모드
    var isEditMode = ObservableBoolean(false)

    // 패키지 노티 리스트
    fun pkgNotiList(pkgName: String) = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        repository.getNotiList(pkgName)
    }.flow
        .cachedIn(viewModelScope)

    // 삭제 목록
    val deleteList = ObservableArrayList<NotiInfo>()

//    fun selectTotalNoti() {
//        viewModelScope.launch(Dispatchers.IO) {
//            removeList.clear()
//            repository.getNotiIdList(pkgName, summaryText).forEach { notiId ->
//                removeList.add(notiId)
//            }
//        }
//    }

    // Noti ID로 스크롤 위치찾기
    suspend fun findPosition(): Int {
        return if (notiId > 0) {
            withContext(Dispatchers.IO) {
                repository.getNotiIdList(pkgName).indexOf(notiId)
            }
        } else 0
    }

    fun clearDeleteList() {
        deleteList.clear()
    }

    suspend fun delete() {
        withContext(Dispatchers.IO) {
            repository.deleteNotiListAndUpdatePkgNoti(deleteList)
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            repository.deletePkgNotiAndNotiInfo(pkgName)
        }
    }
}