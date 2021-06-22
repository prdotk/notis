package com.inging.notis.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.inging.notis.R
import com.inging.notis.constant.AdUnitId
import com.inging.notis.databinding.MainActivityBinding
import com.inging.notis.extension.loadBottomTabPosition
import com.inging.notis.extension.permissionNotification
import com.inging.notis.extension.saveBottomTabPosition
import com.inging.notis.ui.main.more.MoreFragment
import com.inging.notis.ui.main.msg.MsgListFragment
import com.inging.notis.ui.main.notification.NotiListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var binding: MainActivityBinding

    private lateinit var messageFragment: MsgListFragment
    private lateinit var notificationFragment: NotiListFragment
    private lateinit var moreFragment: MoreFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        // 알림 허용 체크
        permissionNotification()

        // 광고
        setupAds()

        messageFragment = MsgListFragment()
        notificationFragment = NotiListFragment()
        moreFragment = MoreFragment()

        // 앱 재기동 시 이미 생성된 프래그먼트 제거
        supportFragmentManager.fragments.forEach {
            supportFragmentManager.beginTransaction()
                .remove(it).commit()
        }

        supportFragmentManager.beginTransaction().run {
            add(R.id.container, messageFragment).show(messageFragment)
            add(R.id.container, notificationFragment).hide(notificationFragment)
            add(R.id.container, moreFragment).hide(moreFragment)
        }.commit()

        binding.bottomNavi.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.tab_message -> {
                    supportFragmentManager.beginTransaction()
                        .show(messageFragment)
                        .hide(notificationFragment)
                        .hide(moreFragment).commit()
                    saveBottomTabPosition(it.itemId)
                }
                R.id.tab_pkg -> {
                    supportFragmentManager.beginTransaction()
                        .hide(messageFragment)
                        .show(notificationFragment)
                        .hide(moreFragment).commit()
                    saveBottomTabPosition(it.itemId)
                }
                R.id.tab_more -> {
                    supportFragmentManager.beginTransaction()
                        .hide(messageFragment)
                        .hide(notificationFragment)
                        .show(moreFragment).commit()
                }
            }
            true
        }
        binding.bottomNavi.selectedItemId = loadBottomTabPosition()
//        binding.bottomNavi.itemIconTintList = null

        // 하단 뱃지 처리
        mainViewModel.totalUnreadCount.observe(this) { total ->
            total?.let { updateBottomNaviBadge(it) }
        }
    }

    override fun onBackPressed() {
        val visibleFragment =
            supportFragmentManager.fragments.findLast { fgm -> fgm.isVisible }
        if ((visibleFragment as MainFragment).finishEditMode()) {
            return
        }
        super.onBackPressed()
    }

    // 하단 탭 뱃지
    private fun updateBottomNaviBadge(count: Int) {
        if (count <= 0) {
            binding.bottomNavi.removeBadge(R.id.tab_message)
        } else {
            binding.bottomNavi.getOrCreateBadge(R.id.tab_message).run {
                backgroundColor = getColor(R.color.badge)
//                number = count
            }
        }
    }

    private fun setupAds() {
//        binding.ads.adUnitId = AD_UNIT_ID
//        binding.ads.loadAd(AdRequest.Builder().build())

        adView = AdView(this)
        binding.layoutAds.addView(adView)
        loadBanner()
    }

    private lateinit var adView: AdView

    // Determine the screen width (less decorations) to use for the ad width.
    // If the ad hasn't been laid out, default to the full screen width.
    private val adSize: AdSize
        get() {
//            val display = windowManager.defaultDisplay
//            val outMetrics = DisplayMetrics()
//            display.getMetrics(outMetrics)

            val outMetrics = resources.displayMetrics

            val density = outMetrics.density

            var adWidthPixels = binding.layoutAds.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    private fun loadBanner() {
        adView.adUnitId = AdUnitId.MAIN_AD

        adView.adSize = adSize

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        val adRequest = AdRequest
            .Builder()
//            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
    }
}