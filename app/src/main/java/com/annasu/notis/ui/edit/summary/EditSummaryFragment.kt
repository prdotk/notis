package com.annasu.notis.ui.edit.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.annasu.notis.R
import com.annasu.notis.data.model.SimpleSummaryData
import com.annasu.notis.databinding.EditSummaryFragmentBinding
import com.annasu.notis.extension.dp2Pixel
import com.annasu.notis.ui.custom.LinearLayoutItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditSummaryFragment : Fragment() {

    private val viewModel: EditSummaryViewModel by activityViewModels()

    private lateinit var binding: EditSummaryFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_summary_fragment, container, false)
        binding.lifecycleOwner = this

        val adapter = EditSummaryAdapter(viewModel.removeList) { pkgName, summaryText, isChecked ->
            // 체크 버튼 클릭 시 액션
            if (isChecked) {
                viewModel.removeList.add(SimpleSummaryData(pkgName, summaryText))
            } else {
                viewModel.removeList.remove(SimpleSummaryData(pkgName, summaryText))
            }
        }

        lifecycleScope.launch {
            viewModel.summaryList.collectLatest {
                adapter.submitData(lifecycle, it)
            }
        }

        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(LinearLayoutItemDecoration(
            requireContext().dp2Pixel(5f), requireContext().dp2Pixel(5f)))
        // 애니메이션 제거
//        binding.recycler.itemAnimator = null

        binding.delete.setOnClickListener {
            lifecycleScope.launch {
                viewModel.remove()
                activity?.finish()
            }
        }

        viewModel.removeList.addOnListChangedCallback(onSelectedItemsChanged)

        return binding.root
    }

    fun updateDeleteBtn(listSize: Int) {
        binding.delete.isEnabled = listSize > 0
    }

    private val onSelectedItemsChanged =
        object : ObservableList.OnListChangedCallback<ObservableList<SimpleSummaryData>>() {
            override fun onChanged(sender: ObservableList<SimpleSummaryData>?) {
                lifecycleScope.launch {
                    updateDeleteBtn(sender?.size ?: 0)
                }
            }

            override fun onItemRangeChanged(sender: ObservableList<SimpleSummaryData>?, positionStart: Int, itemCount: Int) {
                lifecycleScope.launch {
                    updateDeleteBtn(sender?.size ?: 0)
                }
            }

            override fun onItemRangeInserted(sender: ObservableList<SimpleSummaryData>?, positionStart: Int, itemCount: Int) {
                lifecycleScope.launch {
                    updateDeleteBtn(sender?.size ?: 0)
                }
            }

            override fun onItemRangeMoved(sender: ObservableList<SimpleSummaryData>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                lifecycleScope.launch {
                    updateDeleteBtn(sender?.size ?: 0)
                }
            }

            override fun onItemRangeRemoved(sender: ObservableList<SimpleSummaryData>?, positionStart: Int, itemCount: Int) {
                lifecycleScope.launch {
                    updateDeleteBtn(sender?.size ?: 0)
                }
            }
        }
}