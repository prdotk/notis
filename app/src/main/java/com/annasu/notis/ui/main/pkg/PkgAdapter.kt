package com.annasu.notis.ui.main.pkg

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.annasu.notis.data.room.entity.PkgInfoWithNotiInfo

/**
 * Created by datasaver on 2021/04/26.
 */
class PkgAdapter(
    private val listener: (String) -> Unit
) : PagingDataAdapter<PkgInfoWithNotiInfo, PkgViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PkgViewHolder {
        return PkgViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: PkgViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, listener)
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<PkgInfoWithNotiInfo>() {
        override fun areItemsTheSame(oldItem: PkgInfoWithNotiInfo, newItem: PkgInfoWithNotiInfo): Boolean {
            return oldItem.pkgInfo.pkgName == newItem.pkgInfo.pkgName
        }

        override fun areContentsTheSame(oldItem: PkgInfoWithNotiInfo, newItem: PkgInfoWithNotiInfo): Boolean {
            return oldItem == newItem
        }
    }
}