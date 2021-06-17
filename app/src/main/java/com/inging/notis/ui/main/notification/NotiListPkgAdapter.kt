package com.inging.notis.ui.main.notification

import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.inging.notis.data.room.entity.PkgNotiInfo

/**
 * Created by annasu on 2021/04/26.
 */
class NotiListPkgAdapter(
    private val isEditMode: ObservableBoolean,
    private val deletedList: ObservableArrayList<PkgNotiInfo>,
    private val listener: (Int, PkgNotiInfo, Boolean) -> Unit
) : PagingDataAdapter<PkgNotiInfo, NotiListPkgViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiListPkgViewHolder {
        return NotiListPkgViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: NotiListPkgViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, isEditMode, deletedList, listener)
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