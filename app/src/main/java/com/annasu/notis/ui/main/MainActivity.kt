package com.annasu.notis.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.annasu.notis.R
import com.annasu.notis.databinding.MainActivityBinding
import com.annasu.notis.extension.permissionNotification
import com.annasu.notis.ui.main.category.CategoryFragment
import com.annasu.notis.ui.main.pkg.PkgFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var binding: MainActivityBinding

    private val categoryFragment: CategoryFragment by lazy { CategoryFragment() }
    private val pkgFragment: PkgFragment by lazy { PkgFragment() }

    private var activeFragment: Fragment = pkgFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        // 알림 허용 체크
        permissionNotification()

        supportFragmentManager.beginTransaction().run {
            add(R.id.container, categoryFragment).show(categoryFragment)
            add(R.id.container, pkgFragment).hide(pkgFragment)
        }.commit()

        binding.bottomNavi.setOnNavigationItemSelectedListener {
            var ret = false
            when (it.itemId) {
                R.id.tab_category -> {
                    supportFragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(categoryFragment).commit()
                    activeFragment = categoryFragment
                    ret = true
                }
                R.id.tab_pkg -> {
                    supportFragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(pkgFragment).commit()
                    activeFragment = pkgFragment
                    ret = true
                }
            }
            ret
        }
        binding.bottomNavi.selectedItemId = R.id.tab_category

        // 하단 뱃지 처리
        mainViewModel.totalUnreadCount.observe(this) { total ->
            total?.let { updateBottomNaviBadge(it) }
        }
    }

    // 하단 탭 뱃지
    private fun updateBottomNaviBadge(count: Int) {
        if (count <= 0) {
            binding.bottomNavi.removeBadge(R.id.tab_category)
        } else {
            binding.bottomNavi.getOrCreateBadge(R.id.tab_category).run {
                backgroundColor = getColor(R.color.badge)
                number = count
            }
        }
    }
}