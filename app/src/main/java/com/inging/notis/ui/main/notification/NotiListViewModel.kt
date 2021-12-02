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
import kotlinx.coroutines.CoroutineScope
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
//        .map {
//            it.insertSeparators { before: NotiInfo?, after: NotiInfo? ->
//                if (after != null) {
//                    val isDiffDay = after.timestamp.checkDiffDay(before?.timestamp)
//                    if (isDiffDay) {
//                        NotiInfo(
//                            notiId = -1,
//                            senderType = NotiViewType.SEPARATOR,
//                            timestamp = after.timestamp
//                        )
//                    } else null
//                } else null
//            }
//        }
        .cachedIn(viewModelScope)

    // 패키지별 대표노티
    val pkgNotiList = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        repository.getPkgNotiList()
    }.flow
        .cachedIn(viewModelScope)

    val listMode = ObservableInt(-1)

    val selectedNotiList = ObservableArrayList<NotiInfo>()
    val selectedPkgList = ObservableArrayList<PkgNotiInfo>()

    var deleteNotiList = emptyList<NotiInfo>()
    var deletePkgList = emptyList<PkgNotiInfo>()

    fun clearDeleteList() {
        selectedNotiList.clear()
        selectedPkgList.clear()
    }

    // 스와이프로 노티 삭제
//    suspend fun deleteNoti(notiInfo: NotiInfo) {
//        withContext(Dispatchers.IO) {
//            repository.deleteNotiAndUpdatePkgNoti(notiInfo)
//        }
//    }

    // 선택된 노티 삭제
    fun deleteNoti(list: List<NotiInfo>) {
        CoroutineScope(Dispatchers.IO).launch {
            list.forEach { notiInfo ->
                repository.deleteNotiAndUpdatePkgNoti(notiInfo)
            }
        }
    }

    // 선택된 패키지 노티 삭제
//    suspend fun deletePkgNoti(pkgNotiInfo: PkgNotiInfo) {
//        withContext(Dispatchers.IO) {
//            repository.deletePkgNotiAndNotiInfo(pkgNotiInfo)
//        }
//    }

    // 선택된 패키지 노티 삭제
    fun deletePkg(list: List<PkgNotiInfo>) {
        CoroutineScope(Dispatchers.IO).launch {
            list.forEach { pkgNotiInfo ->
                repository.deletePkgNotiAndNotiInfo(pkgNotiInfo)
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllNotMsg()
        }
    }

    suspend fun undoDeleteNoti() {
        withContext(Dispatchers.IO) {
            val idList = deleteNotiList.map { it.notiId }
            repository.updateNotiDeleted(idList, true)
        }
    }

    suspend fun undoRestoreNoti() {
        withContext(Dispatchers.IO) {
            val idList = deleteNotiList.map { it.notiId }
            repository.updateNotiDeleted(idList, false)
        }
    }

    suspend fun undoDeletePkg() {
        withContext(Dispatchers.IO) {
            deletePkgList.forEach { data ->
                repository.updatePkgNotiDeleted(data.pkgNameId, true)
            }
        }
    }

    suspend fun undoRestorePkg() {
        withContext(Dispatchers.IO) {
            deletePkgList.forEach { data ->
                repository.updatePkgNotiDeleted(data.pkgNameId, false)
            }
        }
    }
}