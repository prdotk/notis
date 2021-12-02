package com.inging.notis.ui.main.more

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.lifecycle.ViewModel
import com.inging.notis.data.model.AppInfo
import com.inging.notis.data.room.entity.PkgInfo
import com.inging.notis.extension.getAppName
import com.inging.notis.extension.getInstalledPackage
import com.inging.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: NotiRepository
) : ViewModel() {

    var showSystemApp = false

    var appInfoList: MutableList<AppInfo> = mutableListOf()

    private val installedPackage = context.getInstalledPackage()

    private suspend fun getAppList(context: Context) =
        withContext(Dispatchers.IO) {
            val pkgList = repository.getPkgInfoList()
            installedPackage.map {
                val pkgInfo = pkgList.find { pkgInfo -> pkgInfo.pkgName == it.packageName }
                AppInfo(
                    it.packageName,
                    context.getAppName(it),
                    (it.flags and ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM,
                    pkgInfo?.isBlock ?: true,
                    pkgInfo?.isSave ?: true,
                )
            }
        }

    suspend fun getBlockAppList(context: Context) =
        withContext(Dispatchers.IO) {
            val list = getAppList(context)
            val blockList = list.filter { it.isBlock }.sortedBy { it.label }
            val notBlockList = list.filter { !it.isBlock }.sortedBy { it.label }
            appInfoList.run {
                clear()
                addAll(blockList)
                addAll(notBlockList)
            }
        }

    suspend fun getSaveAppList(context: Context) =
        withContext(Dispatchers.IO) {
            val list = getAppList(context)
            val saveList = list.filter { it.isSave }.sortedBy { it.label }
            val notSaveList = list.filter { !it.isSave }.sortedBy { it.label }
            appInfoList.run {
                clear()
                addAll(saveList)
                addAll(notSaveList)
            }
        }

    fun allBlockSet(enable: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val pkgList = repository.getPkgInfoList()
            installedPackage.map {
                pkgList.find { pkgInfo -> pkgInfo.pkgName == it.packageName }?.let { pkgInfo ->
                    pkgInfo.isBlock = enable
                    repository.savePkgInfo(pkgInfo)
                } ?: run {
                    repository.savePkgInfo(PkgInfo(it.packageName, isBlock = enable, isSave = true))
                }
            }
        }
    }

    fun allSaveSet(enable: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val pkgList = repository.getPkgInfoList()
            installedPackage.map {
                pkgList.find { pkgInfo -> pkgInfo.pkgName == it.packageName }?.let { pkgInfo ->
                    pkgInfo.isSave = enable
                    repository.savePkgInfo(pkgInfo)
                } ?: run {
                    repository.savePkgInfo(PkgInfo(it.packageName, isBlock = true, isSave = enable))
                }
            }
        }
    }

    fun savePkgInfo(info: AppInfo) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.savePkgInfo(info)
        }
    }
}