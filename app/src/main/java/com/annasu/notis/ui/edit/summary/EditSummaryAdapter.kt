package com.annasu.notis.ui.edit.summary

import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.annasu.notis.data.model.SimpleSummaryData
import com.annasu.notis.data.room.entity.SummaryInfo

/**
 * Created by datasaver on 2021/04/26.
 */
class EditSummaryAdapter(
    private val deletedList: ObservableArrayList<SimpleSummaryData>,
    private val listener: (String, String, Boolean) -> Unit
) : PagingDataAdapter<SummaryInfo, EditSummaryViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditSummaryViewHolder {
        return EditSummaryViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: EditSummaryViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, listener, deletedList)
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