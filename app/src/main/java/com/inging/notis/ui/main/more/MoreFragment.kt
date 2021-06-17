package com.inging.notis.ui.main.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.inging.notis.R
import com.inging.notis.databinding.MainMessageFragmentBinding
import com.inging.notis.ui.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreFragment : MainFragment() {

    private val viewModel: MoreViewModel by viewModels()

    private lateinit var binding: MainMessageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_message_fragment, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }
}