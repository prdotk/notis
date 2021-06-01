package com.annasu.notis.extension

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.annasu.notis.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by datasaver on 2021/04/27.
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