package com.annasu.notis.ui.search

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.annasu.notis.R
import com.annasu.notis.databinding.SearchActivityBinding
import com.annasu.notis.ui.edit.search.EditSearchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var binding: SearchActivityBinding

    private val searchFragment: SearchFragment by lazy {
        SearchFragment()
    }

    // activity result
    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        searchFragment.refresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.search_activity)

        // 뒤로가기
        binding.back.setOnClickListener {
            finish()
        }

        // 검색
        binding.input.addTextChangedListener {
            viewModel.word = it.toString()
            if (viewModel.word.isNotEmpty()) {
                searchFragment.search(viewModel.word)
            } else {
                searchFragment.cancel()
            }
        }

        binding.cancel.setOnClickListener {
            binding.input.text.clear()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, searchFragment)
                .commitNow()
        }

        // 상단 메뉴 컨텍스트
        binding.menu.setOnClickListener { v ->
            PopupMenu(this, v).run {
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        // 편집
                        R.id.main_menu_edit -> {
                            if (viewModel.word.isNotEmpty()) {
                                val intent = Intent(this@SearchActivity, EditSearchActivity::class.java)
                                intent.putExtra("WORD", viewModel.word)
                                requestActivity.launch(intent)
                                true
                            } else {
                                false
                            }
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