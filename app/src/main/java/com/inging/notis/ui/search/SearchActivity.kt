package com.inging.notis.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.inging.notis.R
import com.inging.notis.databinding.SearchActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var binding: SearchActivityBinding

    private lateinit var searchFragment: SearchFragment
    private lateinit var searchHistoryFragment: SearchHistoryFragment

    // activity result
    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        searchFragment.refresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(0, 0);
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        binding = DataBindingUtil.setContentView(this, R.layout.search_activity)

        searchFragment = SearchFragment()
        searchHistoryFragment = SearchHistoryFragment()

        // 앱 재기동 시 이미 생성된 프래그먼트 제거
        supportFragmentManager.fragments.forEach {
            supportFragmentManager.beginTransaction()
                .remove(it).commit()
        }

        showSearchHistoryFragment()

        // 뒤로가기
        binding.run {
            back.setOnClickListener {
                finish()
            }

            // 검색
            input.addTextChangedListener {
                viewModel.word = it.toString()
                if (viewModel.word.isNotEmpty()) {
                    cancel.visibility = View.VISIBLE
                    showSearchFragment()
                    searchFragment.search(viewModel.word)
                } else {
                    cancel.visibility = View.GONE
                    showSearchHistoryFragment()
                    searchFragment.cancel()
                }
            }

            input.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    when (keyCode) {
                        EditorInfo.IME_ACTION_SEARCH,
                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER -> {
                            viewModel.saveSearchHistory((v as EditText).text.toString())
                            return@setOnKeyListener true
                        }
                    }
                }
                false
            }

            cancel.setOnClickListener {
                binding.input.text.clear()
            }

            viewModel.wordHistory.observe(this@SearchActivity) {
                if (!it.isNullOrEmpty()) {
                    viewModel.word = it
                    input.setText(it)
//                    showSearchFragment()
//                    searchFragment.search(viewModel.word)
                }
            }
        }
    }

    private fun showSearchFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, searchFragment)
            .commitNow()
    }

    private fun showSearchHistoryFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, searchHistoryFragment)
            .commitNow()
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(0, 0);
//        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
    }
}