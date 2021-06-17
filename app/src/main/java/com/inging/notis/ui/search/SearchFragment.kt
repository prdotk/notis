package com.inging.notis.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.inging.notis.R
import com.inging.notis.databinding.SearchFragmentBinding
import com.inging.notis.extension.dp2Pixel
import com.inging.notis.ui.custom.LinearLayoutItemDecoration
import com.inging.notis.ui.detail.msg.MsgDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by activityViewModels()

    private lateinit var binding: SearchFragmentBinding

    private val adapter: SearchAdapter by lazy {
        SearchAdapter { pkgName, summaryText, notiId ->
            val intent = Intent(context, MsgDetailActivity::class.java)
            intent.putExtra("PKG_NAME", pkgName)
            intent.putExtra("SUMMARY_TEXT", summaryText)
            intent.putExtra("WORD", viewModel.word)
            intent.putExtra("NOTI_ID", notiId)
            startActivity(intent)
        }
    }

    private var oldJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false)
        binding.lifecycleOwner = this

        binding.recycler.run {
//            itemAnimator = null
            this.adapter = this@SearchFragment.adapter
            addItemDecoration(
                LinearLayoutItemDecoration(
                    0,
                    requireContext().dp2Pixel(25f)
                )
            )
        }
//        (binding.recycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
//        binding.recycler.itemAnimator = NoAnimationItemAnimator()

        return binding.root
    }

    fun cancel() {
        oldJob?.cancel()
        lifecycleScope.launch {
            adapter.submitData(lifecycle, PagingData.empty())
        }
    }

    fun search(word: String) {
        cancel()
        oldJob = lifecycleScope.launch {
            viewModel.searchNotiInfoList(word).collectLatest {
                adapter.word = word
                adapter.submitData(lifecycle, it)
                lifecycleScope.launch {
                    delay(100)
                    val firstVisible = (binding.recycler.layoutManager as LinearLayoutManager)
                        .findFirstVisibleItemPosition()
                    if (firstVisible <= 1) {
                        binding.recycler.scrollToPosition(0)
                    }
                    adapter.notifyItemChanged(1)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        lifecycleScope.launch {
            delay(100)
            adapter.notifyDataSetChanged()
        }
    }
}