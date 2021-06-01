package com.annasu.notis.ui.noti

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.annasu.notis.R
import com.annasu.notis.databinding.NotiActivityBinding
import com.annasu.notis.extension.getAppIcon
import com.annasu.notis.extension.loadBitmap
import com.annasu.notis.extension.searchWordHighlight
import com.annasu.notis.ui.edit.noti.EditNotiActivity
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

    // activity result
    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        notiFragment.refresh()
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

        // 상단 메뉴 컨텍스트
        binding.menu.setOnClickListener { v ->
            PopupMenu(this, v).run {
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        // 편집
                        R.id.main_menu_edit -> {
                            val intent = Intent(this@NotiActivity, EditNotiActivity::class.java)
                            intent.putExtra("PKG_NAME", viewModel.pkgName)
                            intent.putExtra("SUMMARY_TEXT", viewModel.summaryText)
                            intent.putExtra("WORD", viewModel.word)
                            requestActivity.launch(intent)
                            true
                        }
                        // 설정
                        R.id.main_menu_setting -> {
                            true
                        }
                        else -> false
                    }
                }
                menuInflater.inflate(R.menu.menu_main_context, menu)
                show()
            }
        }
    }
}