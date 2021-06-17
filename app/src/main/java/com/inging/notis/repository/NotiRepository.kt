package com.inging.notis.repository

import androidx.core.app.NotificationCompat.CATEGORY_MESSAGE
import androidx.paging.PagingSource
import androidx.room.Transaction
import com.inging.notis.data.room.dao.NotiInfoDao
import com.inging.notis.data.room.dao.PkgNotiInfoDao
import com.inging.notis.data.room.dao.SummaryInfoDao
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.data.room.entity.PkgNotiInfo
import com.inging.notis.data.room.entity.SummaryInfo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by annasu on 2021/04/26.
 */
@Singleton
class NotiRepository @Inject constructor(
    private val notiInfoDao: NotiInfoDao,
    private val summaryInfoDao: SummaryInfoDao,
    private val pkgNotiInfoDao: PkgNotiInfoDao
) {
    // 노티 목록
//    fun getNotiList() = notiInfoDao.getAll()

    // 패키지 노티 리스트
    fun getNotiList(pkgName: String) =
        notiInfoDao.getNotiInfoListByPkgName(pkgName)

    // 패키지, 서머리 노티 리스트
    fun getNotiList(pkgName: String, summaryText: String) =
        notiInfoDao.getNotiInfoListByPkgNameAndSummaryText(pkgName, summaryText)

    fun getRecentNoti(pkgName: String, summaryText: String) =
        notiInfoDao.getRecentNotiInfoByPkgNameAndSummaryTextFlow(pkgName, summaryText)

    // 노티 ID 리스트
    suspend fun getNotiIdList(pkgName: String, summaryText: String) =
        notiInfoDao.getNotiIdListByPkgNameAndSummaryText(pkgName, summaryText)

    // 오래된 마지막 노티
    suspend fun getLastNotiId(pkgName: String, summaryText: String) =
        notiInfoDao.getLastNotiId(pkgName, summaryText)

    // 메시지 제외한 전체 노티
    fun getNotiListByNotMsg() = notiInfoDao.getNotiInfoListByNotMsg()

    // 노티 검색
    fun searchNotiInfoList(word: String): PagingSource<Int, NotiInfo> {
        return if (word.substring(0, 1) == "%" || word.substring(0, 1) == "_") {
            notiInfoDao.searchNotiInfoListEscape("""\$word""")
        } else {
            notiInfoDao.searchNotiInfoList(word)
        }
    }

    // 노티 검색, 노티 ID 리스트
    suspend fun searchNotiIdList(word: String): List<Long> {
        return if (word.substring(0, 1) == "%" || word.substring(0, 1) == "_") {
            notiInfoDao.searchNotiIdListEscape("""\$word""")
        } else {
            notiInfoDao.searchNotiIdList(word)
        }
    }

    // 앱 별 노티 목록
    fun getPkgNotiList() = pkgNotiInfoDao.getAll()

    // 앱 별 서머리 목록
    fun getSummaryListByPkgName(pkgName: String) =
        summaryInfoDao.getSummaryInfoListByPkgName(pkgName)

    // 카테고리별 서머리 목록
    fun getSummaryListByCategory(category: String) =
        summaryInfoDao.getSummaryInfoListByCategory(category)

    // 앱 별 서머리 목록
    suspend fun getSummaryIdListByPkgName(pkgName: String) =
        summaryInfoDao.getSummaryIdListByPkgName(pkgName)

    // 카테고리별 서머리 목록
    suspend fun getSummaryIdListByCategory(category: String) =
        summaryInfoDao.getSummaryIdListByCategory(category)

    // 전체 안읽음 갯수
    fun getTotalUnreadCount() =
        summaryInfoDao.getTotalUnreadCount()

    // 카테고리별 안읽음 갯수
    fun getCategoryUnreadCount(category: String) =
        summaryInfoDao.getSummaryUnreadCountByCategory(category)

    // 서머리 읽은 갯수 업데이트
    suspend fun readUpdateSummary(pkgName: String, summaryText: String) {
        summaryInfoDao.getSummaryInfoByPkgNameAndSummaryText(pkgName, summaryText)?.also {
            if (it.unreadCnt > 0) {
                it.unreadCnt = 0
                summaryInfoDao.insert(it)
            }
        }
    }

    // 서머리 최신 노티 업데이트, 삭제 시
    private suspend fun updateSummaryRecentNoti(pkgName: String, summaryText: String) {
        summaryInfoDao.getSummaryInfoByPkgNameAndSummaryText(pkgName, summaryText)
            ?.also { summaryInfo ->
                notiInfoDao.getRecentNotiInfoByPkgNameAndSummaryText(pkgName, summaryText)
                    ?.also { notiInfo ->
                        summaryInfo.recentNotiInfo = notiInfo
                        summaryInfoDao.insert(summaryInfo)
                    }
            }
    }

    // 서머리 삭제
    suspend fun deleteSummaryAndNoti(pkgName: String, summaryText: String) {
        summaryInfoDao.deleteSummaryInfoByPkgNameAndSummaryText(pkgName, summaryText)
        notiInfoDao.deleteNotiInfoByPkgNameAndSummaryText(pkgName, summaryText)
    }

    // 메시지 노티 삭제 후 서머리 업데이트
    suspend fun deleteMsgNotiListAndUpdateSummary(idList: List<Long>, pkgName: String, summaryText: String) {
        notiInfoDao.deleteNotiInfoByIdList(idList)
        if (notiInfoDao.getNotiCntByPkgNameAndSummaryText(pkgName, summaryText) > 0) {
            updateSummaryRecentNoti(pkgName, summaryText)
        } else {
            summaryInfoDao.deleteSummaryInfoByPkgNameAndSummaryText(pkgName, summaryText)
        }
    }

    // 서머리 최신 노티 업데이트, 삭제 시
    private suspend fun updatePkgNotiRecentNoti(pkgName: String) {
        pkgNotiInfoDao.getPkgNotiInfoByPkgName(pkgName)?.also { pkgNotiInfo ->
            notiInfoDao.getRecentNotiInfoByPkgName(pkgName)?.also { notiInfo ->
                pkgNotiInfo.recentNotiInfo = notiInfo
                pkgNotiInfoDao.insert(pkgNotiInfo)
            }
        }
    }

    // 패키지 내부 노티 삭제 후 패키지노티 업데이트
    suspend fun deleteNotiListAndUpdatePkgNoti(notiList: List<NotiInfo>) {
        if (notiList.isNotEmpty()) {
            val pkgName = notiList[0].pkgName
            notiInfoDao.delete(notiList)
            if (notiInfoDao.getNotiCntByPkgNameAndNotMsg(pkgName) > 0) {
                updatePkgNotiRecentNoti(pkgName)
            } else {
                pkgNotiInfoDao.deleteByPkgName(pkgName)
            }
        }
    }

    // 일반 노티 삭제 후 패키지노티 업데이트
    suspend fun deleteNotiAndUpdatePkgNoti(notiInfo: NotiInfo) {
        notiInfoDao.delete(notiInfo)
        if (notiInfoDao.getNotiCntByPkgNameAndNotMsg(notiInfo.pkgName) > 0) {
            updatePkgNotiRecentNoti(notiInfo.pkgName)
        } else {
            pkgNotiInfoDao.deleteByPkgName(notiInfo.pkgName)
        }
    }

    // 앱노티 삭제
    suspend fun deletePkgNotiAndNotiInfo(pkgNotiInfo: PkgNotiInfo) {
        pkgNotiInfoDao.delete(pkgNotiInfo)
        notiInfoDao.deleteNotiInfoByPkgName(pkgNotiInfo.pkgNameId)
    }

    // 앱노티 삭제
    suspend fun deletePkgNotiAndNotiInfo(pkgName: String) {
        pkgNotiInfoDao.deleteByPkgName(pkgName)
        notiInfoDao.deleteNotiInfoByPkgName(pkgName)
    }

    // 메시지 아닌 노티 모두 삭제
    suspend fun deleteAllNotMsg() {
        pkgNotiInfoDao.deleteAll()
        notiInfoDao.deleteAllNotMsg()
    }

    // 메시지 모두 삭제
    suspend fun deleteAllMsg() {
        summaryInfoDao.updateUnreadCntZero()
        summaryInfoDao.deleteAll()
        notiInfoDao.deleteAllMsg()
    }

    // 메시지 모두 읽기
    suspend fun readAllMsg() {
        summaryInfoDao.updateUnreadCntZero()
    }

    @Transaction
    suspend fun insertNoti(info: NotiInfo): Long {
        var notiId = -1L
        // 중복 노티 확인
        if (checkDuplicatedNoti(info)) {
            // 노티 업데이트
            notiId = notiInfoDao.insert(info)
            // 메시지는 서머리 저장 아닌 노티는 패키지 정보 저장
            if (info.category == CATEGORY_MESSAGE) {
                // 서머리 업데이트
                val summaryText = info.summaryText
                val summaryInfo =
                    summaryInfoDao.getSummaryInfoByPkgNameAndSummaryText(info.pkgName, summaryText)
                val unreadCnt = (summaryInfo?.unreadCnt ?: 0) + 1
                summaryInfoDao.insert(SummaryInfo(unreadCnt, info))
            } else {
                // 해당 패키지 노티 갯수
                val notiCount = notiInfoDao.getPkgNotiCount(info.pkgName)
                // 해당 패키지 노티 않읽은 갯수
                val pkgNotiInfo =
                    pkgNotiInfoDao.getPkgNotiInfoByPkgName(info.pkgName)
                val unreadCnt = (pkgNotiInfo?.unreadCnt ?: 0) + 1
                // 인서트 및 업데이트
                pkgNotiInfoDao.insert(PkgNotiInfo(info.pkgName, info, notiCount, unreadCnt))
            }
        }
        return notiId
    }

    // 중복 노티가 있는지 체크, 1초이내 같은 노티면 저장안함
    private suspend fun checkDuplicatedNoti(info: NotiInfo): Boolean {
        notiInfoDao.getRecentNotiInfoByPkgNameAndSummaryTextAndText(
            info.pkgName, info.summaryText, info.text
        )?.let { noti ->
            val time = info.timestamp - noti.timestamp
            // 같은 내용의 노티가 1초이내면 스킵
            return time > 1000
        }
        return true
    }

    // 중복 노티가 있는지 체크, 같은 내용의 노티면 시간만 업데이트하도록.. 정책에 따라
}