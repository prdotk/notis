package com.annasu.notis.ui.main.notification

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.annasu.notis.data.room.entity.NotiInfo

/**
 * Created by annasu on 2021/04/26.
 */
class NotificationAdapter(
    private val listener: (Int, String, String, Boolean) -> Unit
) : PagingDataAdapter<NotiInfo, NotificationViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, listener)
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