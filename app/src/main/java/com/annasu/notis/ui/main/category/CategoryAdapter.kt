package com.annasu.notis.ui.main.category

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.annasu.notis.ui.main.category.view.CategoryPageFragment

/**
 * Created by annasu on 2021/04/26.
 */
class CategoryAdapter(
    fragment: Fragment,
    private val categoryKeys: List<String>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = categoryKeys.size

    override fun createFragment(position: Int): Fragment {
        return CategoryPageFragment.newInstant(categoryKeys[position])
    }
}