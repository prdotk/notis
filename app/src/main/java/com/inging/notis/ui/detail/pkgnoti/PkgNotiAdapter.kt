package com.inging.notis.ui.detail.pkgnoti

import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.inging.notis.data.room.entity.NotiInfo

/**
 * Created by annasu on 2021/04/26.
 */
class PkgNotiAdapter(
    private val isEditMode: ObservableBoolean,
    private val deletedList: ObservableArrayList<NotiInfo>,
    private val listener: (Int, NotiInfo, Boolean) -> Unit
) : PagingDataAdapter<NotiInfo, PkgNotiViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PkgNotiViewHolder {
        return PkgNotiViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: PkgNotiViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, isEditMode, deletedList, listener)
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