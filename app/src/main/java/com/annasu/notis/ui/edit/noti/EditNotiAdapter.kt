package com.annasu.notis.ui.edit.noti

import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.annasu.notis.constant.NotiViewType
import com.annasu.notis.data.room.entity.NotiInfo

/**
 * Created by datasaver on 2021/04/26.
 */
class EditNotiAdapter(
    private val deletedList: ObservableList<Long>,
    private val word: String,
    private val listener: (Long, Boolean) -> Unit
) : PagingDataAdapter<NotiInfo, RecyclerView.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NotiViewType.RIGHT -> EditNotiRightViewHolder.getInstance(parent)
            else -> EditNotiViewHolder.getInstance(parent)
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
            if (holder is EditNotiViewHolder) {
                holder.bind(it, listener, prevItem, nextItem, deletedList, word)
            } else if (holder is EditNotiRightViewHolder) {
                holder.bind(it, listener, prevItem, nextItem, deletedList, word)
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