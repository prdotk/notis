package com.annasu.notis.ui.edit.summary

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.annasu.notis.R
import com.annasu.notis.databinding.EditSummaryActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditSummaryActivity : AppCompatActivity() {

    private val viewModel: EditSummaryViewModel by viewModels()

    private lateinit var binding: EditSummaryActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.edit_summary_activity)

        viewModel.mode = intent?.getIntExtra("MODE", MODE_EDIT_CATEGORY) ?: MODE_EDIT_CATEGORY
        viewModel.category = intent?.getStringExtra("CATEGORY") ?: ""
        viewModel.pkgName = intent?.getStringExtra("PKG_NAME") ?: ""

        binding.back.setOnClickListener {
            finish()
        }

        binding.selectTotal.setOnClickListener {
            viewModel.selectTotalNoti()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, EditSummaryFragment())
                .commitNow()
        }
    }

    companion object {
        const val MODE_EDIT_CATEGORY = 0
        const val MODE_EDIT_PACKAGE = 1
    }
}