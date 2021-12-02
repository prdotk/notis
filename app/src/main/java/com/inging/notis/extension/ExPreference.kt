package com.inging.notis.extension

import android.content.Context
import android.content.SharedPreferences
import com.inging.notis.R
import com.inging.notis.constant.DarkMode
import com.inging.notis.constant.NotiListMode
import com.inging.notis.data.model.IconInfo
import org.json.JSONArray
import org.json.JSONObject

// 앱 SharedPreferences
fun Context.getAppShared(): SharedPreferences {
    return getSharedPreferences("app_shared", Context.MODE_PRIVATE)
}

/**
 * 하단 탭 위치
 */
fun Context.saveBottomTabPosition(value: Int) {
    getAppShared().edit().putInt("bottom_tab", value).apply()
}

fun Context.loadBottomTabPosition(): Int {
    return getAppShared().getInt("bottom_tab", R.id.tab_message)
}

/**
 * 노티 리스트 모드
 */
fun Context.saveNotiListMode(value: Int) {
    getAppShared().edit().putInt("noti_list_mode", value).apply()
}

fun Context.loadNotiListMode(): Int {
    return getAppShared().getInt("noti_list_mode", NotiListMode.PKG)
}

/**
 * 다크 모드
 */
fun Context.saveDarkMode(value: Int) {
    getAppShared().edit().putInt("dark_mode", value).apply()
}

fun Context.loadDarkMode(): Int {
    return getAppShared().getInt("dark_mode", DarkMode.SYSTEM)
}

/**
 * 최신 노티 아이콘 리스트
 */
fun Context.saveRecentIconList(list: List<IconInfo>) {
    // list -> json -> string
    val arrayObj = JSONArray()
    try {
        list.forEach {
            val obj = JSONObject()
            obj.put("pkg_name", it.pkgName)
            obj.put("timestamp", it.timestamp)
            arrayObj.put(obj)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    val value = arrayObj.toString()

    getAppShared().edit().putString("recent_icon_list", value).apply()
}

fun Context.loadRecentIconList(): MutableList<IconInfo> {
    val list = mutableListOf<IconInfo>()
    getAppShared().getString("recent_icon_list", "")?.let {
        try {
            val json = JSONArray(it)
            for (i in 0 until json.length()) {
                val item = json.getJSONObject(i)
                list.add(IconInfo(item.getString("pkg_name"), item.getLong("timestamp")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return list
}