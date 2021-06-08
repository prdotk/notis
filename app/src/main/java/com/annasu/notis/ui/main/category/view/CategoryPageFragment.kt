package com.annasu.notis.ui.main.category.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.annasu.notis.R
import com.annasu.notis.databinding.MainCategoryPageFragmentBinding
import com.annasu.notis.extension.dp2Pixel
import com.annasu.notis.ui.custom.LinearLayoutItemDecoration
import com.annasu.notis.ui.detail.MsgDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryPageFragment : Fragment() {

    private val viewModel: CategoryPageViewModel by viewModels()

    private lateinit var binding: MainCategoryPageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.main_category_page_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this

        val category = arguments?.getString("CATEGORY") ?: ""

        val adapter = CategoryPageAdapter { pkgName, summaryText ->
            val intent = Intent(context, MsgDetailActivity::class.java)
            intent.putExtra("PKG_NAME", pkgName)
            intent.putExtra("SUMMARY_TEXT", summaryText)
            startActivity(intent)
        }

        lifecycleScope.launch {
            viewModel.getSummaryList(category).collectLatest {
                adapter.submitData(lifecycle, it)
            }
        }

        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            LinearLayoutItemDecoration(
                requireContext().dp2Pixel(5f),
                requireContext().dp2Pixel(5f)
            )
        )
        // 애니메이션 제거
//        binding.recycler.itemAnimator = null

        return binding.root
    }

    companion object {
        fun newInstant(category: String): Fragment {
            val fragment = CategoryPageFragment()
            fragment.arguments = Bundle().apply {
                putString("CATEGORY", category)
            }
            return fragment
        }
    }
}