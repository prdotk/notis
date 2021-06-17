package com.inging.notis.ui.summary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.data.room.entity.SummaryInfo
import com.inging.notis.databinding.LayoutCategoryItemBinding
import com.inging.notis.extension.getAppIcon
import com.inging.notis.extension.loadBitmap
import com.inging.notis.extension.toDateOrTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by annasu on 2021/04/26.
 */
class SummaryViewHolder(
    private val binding: LayoutCategoryItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(info: SummaryInfo, listener: (String, String) -> Unit) {
        binding.run {
            // 앱 아이콘
            CoroutineScope(Dispatchers.Main).launch {
                val bitmap = info.recentNotiInfo.largeIcon.loadBitmap(root.context)
                if (bitmap != null) {
                    icon.visibility = View.INVISIBLE
                    largeIcon.visibility = View.VISIBLE
                    largeIcon.setImageBitmap(bitmap)
                } else {
                    icon.visibility = View.VISIBLE
                    largeIcon.visibility = View.GONE
                    icon.setImageDrawable(root.context.getAppIcon(info.recentNotiInfo.pkgName))
                }
            }
            // 타이틀
            title.text = info.recentNotiInfo.summaryText
            // 마지막 알림 시간
            timestamp.text = info.recentNotiInfo.timestamp.toDateOrTime()
            // 노티 내용
            text.text = info.recentNotiInfo.text

            layout.setOnClickListener {
                listener(info.recentNotiInfo.pkgName, info.recentNotiInfo.summaryText)
            }
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): SummaryViewHolder {
            val binding = LayoutCategoryItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            return SummaryViewHolder(binding)
        }
    }
}