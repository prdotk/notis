package com.annasu.notis.ui.search

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.annasu.notis.constant.NotiViewType
import com.annasu.notis.data.room.entity.NotiInfo

/**
 * Created by datasaver on 2021/04/26.
 */
class SearchAdapter(
    private val listener: (String, String, Long) -> Unit
) : PagingDataAdapter<NotiInfo, RecyclerView.ViewHolder>(DiffUtilCallback()) {

    // 검색어
    var word = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NotiViewType.RIGHT -> SearchRightViewHolder.getInstance(parent)
            else -> SearchViewHolder.getInstance(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.senderType ?: NotiViewType.LEFT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var prevItem: NotiInfo? = null
        if (position > 0) {
            prevItem = getItem(position - 1)
        }
        var nextItem: NotiInfo? = null
        if (position + 1 < itemCount) {
            nextItem = getItem(position + 1)
        }
        getItem(position)?.let {
            if (holder is SearchViewHolder) {
                holder.bind(it, listener, prevItem, nextItem, word)
            } else if (holder is SearchRightViewHolder) {
                holder.bind(it, listener, prevItem, nextItem, word)
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<NotiInfo>() {
        override fun areItemsTheSame(oldItem: NotiInfo, newItem: NotiInfo): Boolean {
            return oldItem.notiId == newItem.notiId
        }

        override fun areContentsTheSame(oldItem: NotiInfo, newItem: NotiInfo): Boolean {
            return oldItem == newItem
        }
    }
}