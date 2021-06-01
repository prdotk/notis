package com.annasu.notis.ui.main.pkg

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.annasu.notis.data.room.entity.NotiInfoRecentView

/**
 * Created by datasaver on 2021/04/26.
 */
class PkgNotiAdapter : ListAdapter<NotiInfoRecentView, PkgNotiViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PkgNotiViewHolder {
        return PkgNotiViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: PkgNotiViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<NotiInfoRecentView>() {
        override fun areItemsTheSame(oldItem: NotiInfoRecentView, newItem: NotiInfoRecentView): Boolean {
            return oldItem.notiInfo.notiId == newItem.notiInfo.notiId
        }

        override fun areContentsTheSame(oldItem: NotiInfoRecentView, newItem: NotiInfoRecentView): Boolean {
            return oldItem == newItem
        }
    }
}