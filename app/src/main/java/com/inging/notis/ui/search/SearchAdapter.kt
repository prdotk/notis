package com.inging.notis.ui.search

import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.constant.NotiViewType
import com.inging.notis.data.room.entity.NotiInfo

/**
 * Created by annasu on 2021/04/26.
 */
class SearchAdapter(
    private val listener: (Int, NotiInfo) -> Unit
) : PagingDataAdapter<NotiInfo, RecyclerView.ViewHolder>(DiffUtilCallback()) {

    // 검색어
    var word = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NotiViewType.RIGHT -> SearchRightViewHolder.getInstance(parent)
            NotiViewType.LEFT -> SearchLeftViewHolder.getInstance(parent)
            else -> SearchNotiViewHolder.getInstance(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position)?.let {
            if (it.category == NotificationCompat.CATEGORY_MESSAGE) {
                return it.senderType
            }
        }
        return NotiViewType.NOTI
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        var prevItem: NotiInfo? = null
//        if (position > 0) {
//            prevItem = getItem(position - 1)
//        }
//        var nextItem: NotiInfo? = null
//        if (position + 1 < itemCount) {
//            nextItem = getItem(position + 1)
//        }
        getItem(position)?.let {
            when (holder) {
                is SearchLeftViewHolder -> holder.bind(it, listener, word)//prevItem, nextItem, word)
                is SearchRightViewHolder -> holder.bind(it, listener, word)//prevItem, nextItem, word)
                is SearchNotiViewHolder -> holder.bind(it, listener, word)
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