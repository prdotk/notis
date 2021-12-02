package com.inging.notis

import androidx.multidex.MultiDexApplication
import com.google.android.gms.ads.MobileAds
import com.inging.notis.constant.DarkMode
import com.inging.notis.extension.loadDarkMode
import com.inging.notis.ui.main.more.darkmode.ThemeManager
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by annasu on 2021/04/26.
 */
@HiltAndroidApp
class NotisApplication: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this) {
        }

        setupTheme()
    }

    private fun setupTheme() {
        when (loadDarkMode()) {
            DarkMode.OFF -> ThemeManager.applyTheme(ThemeManager.ThemeMode.LIGHT)
            DarkMode.ON -> ThemeManager.applyTheme(ThemeManager.ThemeMode.DARK)
            else -> ThemeManager.applyTheme(ThemeManager.ThemeMode.DEFAULT)
        }
    }
}