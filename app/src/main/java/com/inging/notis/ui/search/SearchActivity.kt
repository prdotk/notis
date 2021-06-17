package com.inging.notis.ui.search

import android.content.Intent
import android.os.Bundle
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

        // 앱 재기동 시 이미 생성된 프래그먼트 제거
        supportFragmentManager.fragments.forEach {
            supportFragmentManager.beginTransaction()
                .remove(it).commit()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, searchFragment)
            .commitNow()

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
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(0, 0);
//        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
    }
}