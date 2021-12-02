package com.inging.notis.ui.main.more

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.inging.notis.R
import com.inging.notis.databinding.MoreActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreActivity : AppCompatActivity() {

    private val viewModel: MoreViewModel by viewModels()

    private lateinit var binding: MoreActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.more_activity)
        binding.lifecycleOwner = this
    }
}