package com.annasu.notis.constant

import android.app.Notification

/**
 * Created by datasaver on 2021/05/03.
 */
object Constants {
    val categoryMap = mapOf(
        Notification.CATEGORY_MESSAGE to "메시지",
        Notification.CATEGORY_SOCIAL to "소셜",
        Notification.CATEGORY_EMAIL to "이메일",
        Notification.CATEGORY_ALARM to "알람",
        Notification.CATEGORY_TRANSPORT to "미디어",
        Notification.CATEGORY_SYSTEM to "시스템",
        "" to "기타",
        Notification.CATEGORY_CALL to "통화",
        Notification.CATEGORY_STATUS to "정보",
        Notification.CATEGORY_EVENT to "이벤트",
        Notification.CATEGORY_PROMO to "프로모",
        Notification.CATEGORY_PROGRESS to "진행",
        Notification.CATEGORY_SERVICE to "서비스",
        Notification.CATEGORY_RECOMMENDATION to "추천",
        Notification.CATEGORY_REMINDER to "리마인더",
        Notification.CATEGORY_ERROR to "에러",
        Notification.CATEGORY_NAVIGATION to "네비",
    )
}