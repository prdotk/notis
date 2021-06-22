package com.inging.notis.ui.detail.msg

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.inging.notis.constant.NotiViewType
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MsgDetailViewModel @Inject constructor(
    private val repository: NotiRepository
) : ViewModel() {

    var pkgName: String = ""
    var summaryText: String = ""
    var word: String = ""
    var notiId: Long = -1

    // 편집모드
    var isEditMode = ObservableBoolean(false)

    // 보낸 메시지 데이터를 작성하기 위한 최신 데이터
    var recentNoti: NotiInfo? = null

    // 상단 타이틀 표시하기 위한 최신 데이터
    val recentNotiInfo: LiveData<NotiInfo?> by lazy {
        repository.getRecentNoti(pkgName, summaryText).asLiveData()
    }

    // 노티 목록
    val notiInfoList = Pager(
        config = PagingConfig(pageSize = 30)
    ) {
        repository.getNotiList(pkgName, summaryText)
    }.flow
        .cachedIn(viewModelScope)

    // 보낸 데이터 저장
    fun saveMessage(messageText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val notiInfo = NotiInfo(pkgName = pkgName, senderType = NotiViewType.RIGHT)
            notiInfo.category = recentNoti?.category ?: ""
            notiInfo.title = recentNoti?.title ?: ""
            notiInfo.largeIcon = recentNoti?.largeIcon ?: ""

            notiInfo.summaryText = summaryText
            notiInfo.text = messageText
            notiInfo.timestamp = System.currentTimeMillis()
            repository.insertNoti(notiInfo)
        }
    }

    // Noti ID로 스크롤 위치찾기
    suspend fun findPosition(): Int {
        return if (notiId > 0) {
            withContext(Dispatchers.IO) {
                repository.getNotiIdList(pkgName, summaryText).indexOf(notiId)
            }
        } else 0
    }

    // 제일 오래된 노티 ID
    suspend fun getLastNotiId(): Long {
        return withContext(Dispatchers.IO) {
            repository.getLastNotiId(pkgName, summaryText)
        }
    }

    // 노티 읽음 처리
    suspend fun readUpdateSummary() {
        return withContext(Dispatchers.IO) {
            repository.readUpdateSummary(pkgName, summaryText)
        }
    }

    // 삭제 목록
    val deleteList = ObservableArrayList<Long>()

//    fun selectTotalNoti() {
//        viewModelScope.launch(Dispatchers.IO) {
//            deleteList.clear()
//            repository.getNotiIdList(pkgName, summaryText).forEach { notiId ->
//                deleteList.add(notiId)
//            }
//        }
//    }

    fun clearDeleteList() {
        deleteList.clear()
    }

    suspend fun delete(notiId: Long) {
        withContext(Dispatchers.IO) {
            repository.deleteMsgNotiListAndUpdateSummary(notiId, pkgName, summaryText)
        }
    }

    suspend fun delete() {
        withContext(Dispatchers.IO) {
            repository.deleteMsgNotiListAndUpdateSummary(deleteList, pkgName, summaryText)
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            repository.deleteSummaryAndNoti(pkgName, summaryText)
        }
    }
}