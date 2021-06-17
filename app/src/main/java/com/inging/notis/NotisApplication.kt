package com.inging.notis

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by annasu on 2021/04/26.
 */
@HiltAndroidApp
class NotisApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this) {

        }
    }
}