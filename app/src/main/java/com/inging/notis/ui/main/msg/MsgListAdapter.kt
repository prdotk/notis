package com.inging.notis.ui.main.msg

import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.constant.NotiViewType
import com.inging.notis.data.model.SimpleSummaryData
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.data.room.entity.SummaryInfo

/**
 * Created by annasu on 2021/04/26.
 */
class MsgListAdapter(
    private val isEditMode: ObservableBoolean,
    private val deletedList: ObservableArrayList<SimpleSummaryData>,
    private val listener: (Int, NotiInfo, Boolean) -> Unit
) : PagingDataAdapter<SummaryInfo, RecyclerView.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NotiViewType.HEADER -> AdHeaderViewHolder.getInstance(parent)
            else -> MsgListViewHolder.getInstance(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.recentNotiInfo?.senderType ?: NotiViewType.LEFT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (holder) {
                is AdHeaderViewHolder -> holder.bind()
                is MsgListViewHolder -> holder.bind(it, isEditMode, deletedList, listener)
            }
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