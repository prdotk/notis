package com.annasu.notis.ui.main.pkg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.annasu.notis.data.room.entity.NotiInfoRecentView
import com.annasu.notis.databinding.LayoutPkgNotiItemBinding

/**
 * Created by datasaver on 2021/04/26.
 */
class PkgNotiViewHolder(
    private val binding: LayoutPkgNotiItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(info: NotiInfoRecentView) {
        binding.run {
            title.text = info.notiInfo.title
            text.text = info.notiInfo.text
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): PkgNotiViewHolder {
            val binding = LayoutPkgNotiItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            return PkgNotiViewHolder(binding)
        }
    }
}