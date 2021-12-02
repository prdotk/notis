package com.inging.notis.ui.main.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.databinding.LayoutNotiSeparatorItemBinding
import com.inging.notis.extension.toDate

/**
 * Created by annasu on 2021/04/26.
 */
class NotiListAllSeparatorViewHolder(
    private val binding: LayoutNotiSeparatorItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(info: NotiInfo) {
        binding.run {
            date.text = info.timestamp.toDate(root.context)
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): NotiListAllSeparatorViewHolder {
            val binding = LayoutNotiSeparatorItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return NotiListAllSeparatorViewHolder(binding)
        }
    }
}