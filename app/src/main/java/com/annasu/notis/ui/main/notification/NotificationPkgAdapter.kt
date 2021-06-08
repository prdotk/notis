package com.annasu.notis.ui.main.notification

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.annasu.notis.data.room.entity.PkgNotiInfo

/**
 * Created by annasu on 2021/04/26.
 */
class NotificationPkgAdapter(
    private val listener: (Int, String, String, Boolean) -> Unit
) : PagingDataAdapter<PkgNotiInfo, NotificationPkgViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationPkgViewHolder {
        return NotificationPkgViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: NotificationPkgViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, listener)
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<PkgNotiInfo>() {
        override fun areItemsTheSame(oldItem: PkgNotiInfo, newItem: PkgNotiInfo): Boolean {
            return oldItem.pkgNameId == newItem.pkgNameId
        }

        override fun areContentsTheSame(oldItem: PkgNotiInfo, newItem: PkgNotiInfo): Boolean {
            return oldItem == newItem
        }
    }
}