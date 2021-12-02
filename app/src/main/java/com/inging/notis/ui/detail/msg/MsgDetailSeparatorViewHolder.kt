package com.inging.notis.ui.detail.msg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.databinding.LayoutSeparatorItemBinding
import com.inging.notis.extension.toDate

/**
 * Created by annasu on 2021/04/26.
 */
class MsgDetailSeparatorViewHolder(
    private val binding: LayoutSeparatorItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(info: NotiInfo) {
        binding.run {
            date.text = info.timestamp.toDate(root.context)
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): MsgDetailSeparatorViewHolder {
            val binding = LayoutSeparatorItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return MsgDetailSeparatorViewHolder(binding)
        }
    }
}