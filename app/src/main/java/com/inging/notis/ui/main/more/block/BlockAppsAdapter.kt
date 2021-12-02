package com.inging.notis.ui.main.more.block

import android.view.ViewGroup
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.data.model.AppInfo

/**
 * Created by annasu on 2021/04/26.
 */
class BlockAppsAdapter(
    private val allOnOff: ObservableInt,
    private val listener: (Int, AppInfo) -> Unit
) : ListAdapter<AppInfo, RecyclerView.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BlockAppsViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (holder) {
                is BlockAppsViewHolder -> holder.bind(it, allOnOff, listener)
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.pkgName == newItem.pkgName
        }

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem == newItem
        }
    }
}