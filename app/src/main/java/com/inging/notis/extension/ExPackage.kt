package com.inging.notis.extension

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
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

suspend fun Context.getAppIcon(): Drawable? {
    return getAppIcon(packageName)
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

fun Context.getAppName(info: ApplicationInfo): String {
    return try {
        packageManager.getApplicationLabel(info).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        packageName
    } catch (e: NullPointerException) {
        packageName
    }
}

/**
 * 설치되 패키지 필터링
 * 실행 할 수 있는 앱 목록
 */
fun Context.getInstalledApps(): List<ApplicationInfo> {
    return getInstalledPackage().filter {
        // 아이콘, 이름 체크
//                it.icon > 0
//                    && !it.name.isNullOrEmpty()
        // 시스템 앱 플래그
//                ((it.flags and FLAG_SYSTEM) == 0 || (it.flags and FLAG_UPDATED_SYSTEM_APP) != 0)
        // 실행 할 수 있는지 체크
        checkLaunchIntent(it.packageName)
    }
}

/**
 * 설치된 패키지 목록
 */
fun Context.getInstalledPackage(): List<ApplicationInfo> {
    return packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
}

/**
 * 실행 할 수 있는 앱인지 체크
 */
fun Context.checkLaunchIntent(packageName: String): Boolean {
    return packageManager.getLaunchIntentForPackage(packageName) != null
}