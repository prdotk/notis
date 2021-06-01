package com.annasu.notis.ui.summary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.annasu.notis.R
import com.annasu.notis.databinding.SummaryFragmentBinding
import com.annasu.notis.extension.dp2Pixel
import com.annasu.notis.ui.custom.LinearLayoutItemDecoration
import com.annasu.notis.ui.noti.NotiActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SummaryFragment : Fragment() {

    private val viewModel: SummaryViewModel by activityViewModels()

    private lateinit var binding: SummaryFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.summary_fragment, container, false)
        binding.lifecycleOwner = this

        val adapter = SummaryAdapter { pkgName, summaryText ->
            val intent = Intent(context, NotiActivity::class.java)
            intent.putExtra("PKG_NAME", pkgName)
            intent.putExtra("SUMMARY_TEXT", summaryText)
            startActivity(intent)
        }

        lifecycleScope.launch {
            viewModel.getTitleList(viewModel.pkgName).collectLatest {
                adapter.submitData(lifecycle, it)
            }
        }

        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(LinearLayoutItemDecoration(
            requireContext().dp2Pixel(5f), requireContext().dp2Pixel(5f)))
        // 애니메이션 제거
//        binding.recycler.itemAnimator = null

        return binding.root
    }
}