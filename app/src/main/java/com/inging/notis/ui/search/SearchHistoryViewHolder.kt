package com.inging.notis.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.constant.ClickMode
import com.inging.notis.data.room.entity.SearchHistoryInfo
import com.inging.notis.databinding.LayoutSearchHistoryItemBinding

/**
 * Created by annasu on 2021/04/26.
 */
class SearchHistoryViewHolder(
    private val binding: LayoutSearchHistoryItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        info: SearchHistoryInfo,
        listener: (Int, SearchHistoryInfo) -> Unit
    ) {
        binding.run {
            word.text = info.word

            layout.setOnClickListener {
                listener(ClickMode.LAYOUT, info)
            }

            delete.setOnClickListener {
                listener(ClickMode.BUTTON_DELETE, info)
            }
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): SearchHistoryViewHolder {
            val binding = LayoutSearchHistoryItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return SearchHistoryViewHolder(binding)
        }
    }
}