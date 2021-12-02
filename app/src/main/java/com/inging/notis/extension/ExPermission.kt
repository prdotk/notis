package com.inging.notis.extension

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import com.inging.notis.ui.main.MainActivity


/**
 * Created by annasu on 2021/03/03.
 */

/**
 * 사용정보 접근 권한
 */
fun Context.permissionGetUsageStats(): Boolean {
    (getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager)?.let { appOps ->
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpRaw(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        } else {
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        }
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true
        }
        appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
            applicationContext.packageName, object : AppOpsManager.OnOpChangedListener {
            override fun onOpChanged(op: String?, packageName: String?) {
                packageName?.let {
                    val m = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        appOps.unsafeCheckOpRaw(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), it)
                    } else {
                        appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), it)
                    }
                    if (m != AppOpsManager.MODE_ALLOWED) {
                        return
                    }
                    appOps.stopWatchingMode(this)
                    val intent = Intent(this@permissionGetUsageStats, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    applicationContext.startActivity(intent)
                }
            }
        })
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }
    return false
}

/**
 * 다른 앱 위에 표시 권한
 */
fun Context.permissionManageOverlay(): Boolean {
    (getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager)?.let { appOps ->
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpRaw(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Process.myUid(), packageName)
        } else {
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Process.myUid(), packageName)
        }
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true
        }
        appOps.startWatchingMode(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
            applicationContext.packageName, object : AppOpsManager.OnOpChangedListener {
            override fun onOpChanged(op: String?, packageName: String?) {
                packageName?.let {
                    val m = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        appOps.unsafeCheckOpRaw(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Process.myUid(), it)
                    } else {
                        appOps.checkOpNoThrow(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Process.myUid(), it)
                    }
                    if (m != AppOpsManager.MODE_ALLOWED) {
                        return
                    }
                    appOps.stopWatchingMode(this)
                    val intent = Intent(this@permissionManageOverlay, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    applicationContext.startActivity(intent)
                }
            }
        })
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        startActivity(intent)
    }
    return false
}

/**
 * 알림 접근 허용
 */
fun Context.permissionNotification() {
    // 알림 접근 설정 페이지 이동
    startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
}