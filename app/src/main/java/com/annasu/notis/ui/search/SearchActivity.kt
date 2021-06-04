package com.annasu.notis.ui.search

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.annasu.notis.R
import com.annasu.notis.databinding.SearchActivityBinding
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

        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

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
    }

    override fun finish() {
        super.finish()

        this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
    }
}