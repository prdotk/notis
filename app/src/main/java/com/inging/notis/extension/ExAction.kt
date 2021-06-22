package com.inging.notis.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.inging.notis.constant.ServiceCommandType
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.service.NotisNotificationListenerService

//특정 패키지명의 앱 실행(설치여부 확인후 실행필요)
fun Context.executeLocalAppPackage(packageName: String) {
    packageManager.getLaunchIntentForPackage(packageName)?.let {
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//       it.action = Intent.ACTION_MAIN
        startActivity(it)
    }
}

//특정 패키지명의 앱의 설치경로(PlayStore) 이동처리
fun Context.executeStoreAppPackage(packageName: String) {
    val url = "market://details?id=$packageName"
    val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(i)
}

// 노티 액션 실행
fun Context.runContentIntent(info: NotiInfo) {
    val intent = Intent(this, NotisNotificationListenerService::class.java)
    intent.putExtra("COMMAND_TYPE", ServiceCommandType.RUN_CONTENT_INTENT)
    intent.putExtra("PKG_NAME", info.pkgName)
    intent.putExtra("NOTI_ID", info.notiId)
    startService(intent)
}

fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(null, text)
    clipboard.setPrimaryClip(clip)
}