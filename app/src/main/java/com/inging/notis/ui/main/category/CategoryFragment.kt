package com.inging.notis.ui.main.category

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.inging.notis.R
import com.inging.notis.databinding.MainCategoryFragmentBinding
import com.inging.notis.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private val viewModel: CategoryViewModel by viewModels()

    private lateinit var binding: MainCategoryFragmentBinding

    private var currentTabPosition: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_category_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.pager.adapter = CategoryAdapter(this, viewModel.categoryKeys)

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
//            tab.text = viewModel.categoryMap[viewModel.categoryKeys[position]]
            tab.setCustomView(R.layout.layout_tab_badge)
            tab.customView?.findViewById<TextView>(R.id.title)?.text =
                viewModel.categoryMap[viewModel.categoryKeys[position]]
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabPosition = tab?.position ?: 0
            }
        })

        // 카테고리별 안읽은 갯수
        viewModel.categoryKeys.forEachIndexed { index, category ->
            viewModel.getCategoryUnreadCount(category).observe(viewLifecycleOwner) { unread ->
                binding.tabLayout.getTabAt(index)?.run {
                    customView?.findViewById<TextView>(R.id.unread)?.run {
                        when {
                            unread == null -> visibility = View.GONE
                            unread > 0  ->  {
                                visibility = View.VISIBLE
                                text = unread.toString()
                            }
                            else -> visibility = View.GONE
                        }
                    }
                }
            }
        }

        // 검색
        binding.search.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)
        }

        // 상단 메뉴 컨텍스트
        binding.menu.setOnClickListener { v ->
            PopupMenu(requireContext(), v).run {
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        // 편집
                        R.id.main_menu_edit -> {
//                            val intent = Intent(context, EditSummaryActivity::class.java)
//                            intent.putExtra("MODE", MODE_EDIT_CATEGORY)
//                            intent.putExtra("CATEGORY", viewModel.categoryKeys[currentTabPosition])
//                            startActivity(intent)
                            true
                        }
                        else -> false
                    }
                }
                menuInflater.inflate(R.menu.menu_main_msg_context, menu)
                show()
            }
        }
    }
}