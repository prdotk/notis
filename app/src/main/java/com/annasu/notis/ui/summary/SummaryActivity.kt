package com.annasu.notis.ui.summary

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.annasu.notis.R
import com.annasu.notis.databinding.SummaryActivityBinding
import com.annasu.notis.extension.getAppIcon
import com.annasu.notis.extension.getAppName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SummaryActivity : AppCompatActivity() {

    private val viewModel: SummaryViewModel by viewModels()

    private lateinit var binding: SummaryActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.summary_activity)

        val pkgName = intent?.getStringExtra("PKG_NAME") ?: ""

        viewModel.pkgName = pkgName

        // 패키지 이름
        binding.title.text = getAppName(pkgName)

        // 앱 아이콘
        lifecycleScope.launch(Dispatchers.Main) {
            binding.icon.setImageDrawable(getAppIcon(pkgName))
        }

        binding.back.setOnClickListener {
            finish()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SummaryFragment())
                .commitNow()
        }

        // 상단 메뉴 컨텍스트
        binding.menu.setOnClickListener { v ->
            PopupMenu(this, v).run {
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        // 편집
                        R.id.main_menu_edit -> {
//                            val intent = Intent(this@SummaryActivity, EditSummaryActivity::class.java)
//                            intent.putExtra("MODE", MODE_EDIT_PACKAGE)
//                            intent.putExtra("PKG_NAME", viewModel.pkgName)
//                            startActivity(intent)
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