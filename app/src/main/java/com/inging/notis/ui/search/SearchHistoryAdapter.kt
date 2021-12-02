package com.inging.notis.ui.search

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.data.room.entity.SearchHistoryInfo

/**
 * Created by annasu on 2021/04/26.
 */
class SearchHistoryAdapter(
    private val listener: (Int, SearchHistoryInfo) -> Unit
) : PagingDataAdapter<SearchHistoryInfo, RecyclerView.ViewHolder>(DiffUtilCallback()) {

    // 검색어
    var word = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchHistoryViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (holder) {
                is SearchHistoryViewHolder -> holder.bind(it, listener)
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<SearchHistoryInfo>() {
        override fun areItemsTheSame(oldItem: SearchHistoryInfo, newItem: SearchHistoryInfo): Boolean {
            return oldItem.word == newItem.word
        }

        override fun areContentsTheSame(oldItem: SearchHistoryInfo, newItem: SearchHistoryInfo): Boolean {
            return oldItem == newItem
        }
    }
}