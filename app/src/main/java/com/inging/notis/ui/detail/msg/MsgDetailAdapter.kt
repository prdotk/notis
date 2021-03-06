package com.inging.notis.ui.detail.msg

import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.constant.NotiViewType
import com.inging.notis.data.room.entity.NotiInfo

/**
 * Created by annasu on 2021/04/26.
 */
class MsgDetailAdapter(
    private val word: String,
    private val isEditMode: ObservableBoolean,
    private val deletedList: ObservableArrayList<Long>,
    private val listener: (Int, NotiInfo, Boolean, Int) -> Unit
) : PagingDataAdapter<NotiInfo, RecyclerView.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NotiViewType.SEPARATOR -> MsgDetailSeparatorViewHolder.getInstance(parent)
            NotiViewType.RIGHT -> MsgDetailRightViewHolder.getInstance(parent)
            else -> MsgDetailLeftViewHolder.getInstance(parent)
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
            when (holder) {
                is MsgDetailViewHolder -> holder.bind(
                    it, prevItem, nextItem, word,
                    isEditMode, deletedList, listener,
                    position
                )
                is MsgDetailSeparatorViewHolder -> holder.bind(it)
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