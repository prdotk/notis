package com.inging.notis.extension

import android.content.Context
import android.content.SharedPreferences
import com.inging.notis.R
import com.inging.notis.constant.NotiListMode

// 앱 SharedPreferences
fun Context.getAppShared(): SharedPreferences {
    return getSharedPreferences("app_shared", Context.MODE_PRIVATE)
}

// 하단 탭 위치 로드
fun Context.loadBottomTabPosition(): Int {
    return getAppShared().getInt("bottom_tab", R.id.tab_message)
}

// 하단 탭 위치 저장
fun Context.saveBottomTabPosition(value: Int) {
    getAppShared().edit().putInt("bottom_tab", value).apply()
}

fun Context.loadNotiListMode(): Int {
    return getAppShared().getInt("noti_list_mode", NotiListMode.ALL)
}

fun Context.saveNotiListMode(value: Int) {
    getAppShared().edit().putInt("noti_list_mode", value).apply()
}
