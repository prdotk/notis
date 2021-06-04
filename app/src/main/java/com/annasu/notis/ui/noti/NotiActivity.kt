package com.annasu.notis.ui.noti

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.lifecycleScope
import com.annasu.notis.R
import com.annasu.notis.databinding.NotiActivityBinding
import com.annasu.notis.extension.getAppIcon
import com.annasu.notis.extension.loadBitmap
import com.annasu.notis.extension.searchWordHighlight
import com.annasu.notis.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotiActivity : AppCompatActivity() {

    private val viewModel: NotiViewModel by viewModels()

    private lateinit var binding: NotiActivityBinding

    private val notiFragment: NotiFragment by lazy {
        NotiFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.noti_activity)

        viewModel.pkgName = intent?.getStringExtra("PKG_NAME") ?: ""
        viewModel.summaryText = intent?.getStringExtra("SUMMARY_TEXT") ?: ""
        viewModel.word = intent?.getStringExtra("WORD") ?: ""
        viewModel.notiId = intent?.getLongExtra("NOTI_ID", -1) ?: -1

        // 패키지 이름
        binding.title.text = viewModel.summaryText
        binding.title.searchWordHighlight(viewModel.word)

        // 앱 아이콘
        viewModel.recentNotiInfo.observe(this) {
            it?.let { info ->
                lifecycleScope.launch(Dispatchers.Main) {
                    val bitmap = info.largeIcon.loadBitmap(this@NotiActivity)
                    if (bitmap != null) {
                        binding.icon.visibility = View.INVISIBLE
                        binding.largeIcon.visibility = View.VISIBLE
                        binding.largeIcon.setImageBitmap(bitmap)
                    } else {
                        binding.icon.visibility = View.VISIBLE
                        binding.largeIcon.visibility = View.GONE
                        binding.icon.setImageDrawable(getAppIcon(info.pkgName))
                    }
                }
            }
        }

        // 뒤로가기
        binding.back.setOnClickListener {
            finish()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, notiFragment)
                .commitNow()
        }

        // 검색
        binding.search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // 취소
        binding.cancel.setOnClickListener {
            lifecycleScope.launch {
                finishEditMode()
            }
        }

        viewModel.isMsgEditMode.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender is ObservableBoolean) {
                    val isEditMode = sender.get()
                    binding.search.isVisible = !isEditMode
                    binding.cancel.isVisible = isEditMode
                    binding.fab.isVisible = isEditMode
                }
            }
        })

        binding.fab.setOnClickListener {
            if (viewModel.isMsgEditMode.get()) {
                lifecycleScope.launch {
                    removeMessage()
                }
            } else {
                // 모두 읽기 처리
            }
        }
    }

    override fun onBackPressed() {
        if (viewModel.isMsgEditMode.get()) {
            finishEditMode()
        } else {
            super.onBackPressed()
        }
    }

    private fun finishEditMode() {
        viewModel.clearRemoveList()
        viewModel.isMsgEditMode.set(false)
    }

    private fun removeMessage() {
        lifecycleScope.launch {
            viewModel.remove()
            finishEditMode()
        }
    }
}