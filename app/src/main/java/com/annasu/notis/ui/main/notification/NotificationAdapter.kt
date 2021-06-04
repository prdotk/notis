package com.annasu.notis.ui.main.notification

import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.annasu.notis.data.model.SimpleSummaryData
import com.annasu.notis.data.room.entity.SummaryInfo

/**
 * Created by datasaver on 2021/04/26.
 */
class NotificationAdapter(
    private val isEditMode: ObservableBoolean,
    private val deletedList: ObservableArrayList<SimpleSummaryData>,
    private val listener: (Int, String, String, Boolean) -> Unit
) : PagingDataAdapter<SummaryInfo, NotificationViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
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