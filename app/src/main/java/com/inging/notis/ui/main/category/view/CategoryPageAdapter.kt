package com.inging.notis.ui.main.category.view

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.inging.notis.data.room.entity.SummaryInfo

/**
 * Created by annasu on 2021/04/26.
 */
class CategoryPageAdapter(
    private val listener: (String, String) -> Unit
) : PagingDataAdapter<SummaryInfo, CategoryPageViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryPageViewHolder {
        return CategoryPageViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: CategoryPageViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, listener)
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<SummaryInfo>() {
        override fun areItemsTheSame(oldItem: SummaryInfo, newItem: SummaryInfo): Boolean {
            return oldItem.recentNotiInfo.summaryText == newItem.recentNotiInfo.summaryText
        }

        override fun areContentsTheSame(oldItem: SummaryInfo, newItem: SummaryInfo): Boolean {
            return oldItem == newItem
        }
    }
}