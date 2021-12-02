package com.inging.notis.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.inging.notis.R
import com.inging.notis.constant.ClickMode
import com.inging.notis.databinding.SearchHistoryFragmentBinding
import com.inging.notis.extension.dp2Pixel
import com.inging.notis.ui.custom.LinearLayoutItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchHistoryFragment : Fragment() {

    private val viewModel: SearchViewModel by activityViewModels()

    private lateinit var binding: SearchHistoryFragmentBinding

    private val _adapter: SearchHistoryAdapter by lazy {
        SearchHistoryAdapter { mode, info ->
            when (mode) {
                ClickMode.LAYOUT -> {
                    viewModel.wordHistory.value = info.word
                    viewModel.saveSearchHistory(info.word)
                }
                ClickMode.BUTTON_DELETE -> {
                    viewModel.deleteSearchHistory(info.word)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.search_history_fragment, container, false)
        binding.lifecycleOwner = this

        binding.recycler.run {
//            itemAnimator = null
            adapter = _adapter
            addItemDecoration(
                LinearLayoutItemDecoration(
                    0,
                    requireContext().dp2Pixel(25f)
                )
            )
        }

        lifecycleScope.launch {
            viewModel.searchHistoryList.collectLatest {
                _adapter.submitData(it)
            }
        }

        return binding.root
    }
}