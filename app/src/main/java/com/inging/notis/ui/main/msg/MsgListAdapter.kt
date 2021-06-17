package com.inging.notis.ui.main.msg

import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.inging.notis.data.model.SimpleSummaryData
import com.inging.notis.data.room.entity.SummaryInfo

/**
 * Created by annasu on 2021/04/26.
 */
class MsgListAdapter(
    private val isEditMode: ObservableBoolean,
    private val deletedList: ObservableArrayList<SimpleSummaryData>,
    private val listener: (Int, String, String, Boolean) -> Unit
) : PagingDataAdapter<SummaryInfo, MsgListViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgListViewHolder {
        return MsgListViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: MsgListViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, isEditMode, deletedList, listener)
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