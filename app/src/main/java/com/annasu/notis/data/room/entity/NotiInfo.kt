package com.annasu.notis.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by datasaver on 2021/04/26.
 * https://developer.android.com/reference/android/app/Notification
 */
@Entity(ignoredColumns = ["isChecked"])
data class NotiInfo(

    @PrimaryKey(autoGenerate = true)
    val notiId: Long = 0,

    // 카테고리
    var category: String = "",

    // 패키지 네임
    var pkgName: String = "",

    // 패키지별 노티 ID
    var pkgNotiId: Long = 0,

    // 저장 시간
    var timestamp: Long = -1,

    // 타이틀, 카톡의 경우 보낸 사람
    var title: String = "",

    // 내용
    var text: String = "",

    // 서브 텍스트, 카톡의 경우 그룹명
    var subText: String = "",

    // 노티 그룹 관련 정보, 카톡의 경우 그룹명
    var summaryText: String = "",

    // 아이콘, 현재 사용 안함
    var icon: Int = 0,

    // 로컬 이미지 파일 경로, 캐시에 jpg 파일로 저장됨
    var largeIcon: String = "",

    // 0: 받은 메시지, 1: 보낸 메시지
    val senderType: Int

) : IgnoreInfo()