package com.inging.notis.extension

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.inging.notis.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Created by annasu on 2021/04/27.
 */

/**
 * 패키지명으로 아이콘 이미지 가져오기
 */
suspend fun Context.getAppIcon(pkgName: String): Drawable? {
    return try {
        withContext(Dispatchers.Default) {
            packageManager.getApplicationIcon(pkgName)
        }
    } catch (e: Exception) {
        ContextCompat.getDrawable(this, R.drawable.ic_app_default)
    }
}

/**
 * 패키지명으로 아이콘 이미지 가져오기
 */
suspend fun Context.getAppIcon(pkgName: String, iconId: Int): Drawable? {
    return try {
        withContext(Dispatchers.Default) {
            val resources = packageManager.getResourcesForApplication(pkgName)
            ResourcesCompat.getDrawable(resources, iconId, null)
        }
    } catch (e: Exception) {
        ContextCompat.getDrawable(this, R.drawable.ic_app_default)
    }
}

/**
 * 패키징명으로 앱 이름 가져옴
 */
fun Context.getAppName(packageName: String): String {
    return try {
        packageManager.getApplicationLabel(
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        ).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        packageName
    } catch (e: NullPointerException) {
        packageName
    }
}

//특정 패키지명의 앱 실행(설치여부 확인후 실행필요)
fun Context.executeLocalAppPackage(packageName: String) {
   packageManager.getLaunchIntentForPackage(packageName)?.let {
       it.addFlags(FLAG_ACTIVITY_NEW_TASK)
//       it.action = Intent.ACTION_MAIN
       startActivity(it)
   }
}

//특정 패키지명의 앱의 설치경로(PlayStore) 이동처리
fun Context.executeStoreAppPackage(packageName: String) {
    val url = "market://details?id=$packageName"
    val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    i.addFlags(FLAG_ACTIVITY_NEW_TASK)
    startActivity(i)
}
