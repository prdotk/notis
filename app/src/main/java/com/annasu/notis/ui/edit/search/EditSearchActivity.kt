package com.annasu.notis.ui.edit.search

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.annasu.notis.R
import com.annasu.notis.databinding.EditNotiActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditSearchActivity : AppCompatActivity() {

    private val viewModel: EditSearchViewModel by viewModels()

    private lateinit var binding: EditNotiActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.edit_noti_activity)

        viewModel.word = intent?.getStringExtra("WORD") ?: ""

        binding.back.setOnClickListener {
            finish()
        }

        binding.selectTotal.setOnClickListener {
            viewModel.selectTotalNoti()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, EditSearchFragment())
                .commitNow()
        }
    }
}