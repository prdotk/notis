package com.annasu.notis.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.annasu.notis.R
import com.annasu.notis.databinding.MainActivityBinding
import com.annasu.notis.extension.permissionNotification
import com.annasu.notis.ui.main.message.MessageFragment
import com.annasu.notis.ui.main.notification.NotificationFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var binding: MainActivityBinding

    private lateinit var messageFragment: MessageFragment
    private lateinit var notificationFragment: NotificationFragment

    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        // 알림 허용 체크
        permissionNotification()

        messageFragment = MessageFragment()
        notificationFragment = NotificationFragment()
        activeFragment = messageFragment

        // 앱 재기동 시 이미 생성된 프래그먼트 제거
        supportFragmentManager.fragments.forEach {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }

        supportFragmentManager.beginTransaction().run {
            add(R.id.container, messageFragment).show(messageFragment)
            add(R.id.container, notificationFragment).hide(notificationFragment)
        }.commit()

        binding.bottomNavi.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.tab_message -> {
                    supportFragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(messageFragment).commit()
                    activeFragment = messageFragment
                }
                R.id.tab_pkg -> {
                    supportFragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(notificationFragment).commit()
                    activeFragment = notificationFragment

                    messageFragment.finishEditMode()
                }
                R.id.tab_more -> {
                    supportFragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(notificationFragment).commit()
                    activeFragment = notificationFragment

                    messageFragment.finishEditMode()
                }
            }
            true
        }
        binding.bottomNavi.selectedItemId = R.id.tab_message
//        binding.bottomNavi.itemIconTintList = null

        // 하단 뱃지 처리
        mainViewModel.totalUnreadCount.observe(this) { total ->
            total?.let { updateBottomNaviBadge(it) }
        }
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
}