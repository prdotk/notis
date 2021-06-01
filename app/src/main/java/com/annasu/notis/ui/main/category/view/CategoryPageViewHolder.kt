package com.annasu.notis.ui.main.category.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.annasu.notis.data.room.entity.SummaryInfo
import com.annasu.notis.databinding.LayoutCategoryItemBinding
import com.annasu.notis.extension.getAppIcon
import com.annasu.notis.extension.loadBitmap
import com.annasu.notis.extension.toDateOrTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by datasaver on 2021/04/26.
 */
class CategoryPageViewHolder(
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
                    smallIcon.visibility = View.VISIBLE
                    smallIcon.setImageDrawable(root.context.getAppIcon(info.recentNotiInfo.pkgName))
                } else {
                    icon.visibility = View.VISIBLE
                    icon.setImageDrawable(root.context.getAppIcon(info.recentNotiInfo.pkgName))
                    largeIcon.visibility = View.GONE
                    smallIcon.visibility = View.GONE
                }
            }
            // 타이틀
            title.text = info.recentNotiInfo.summaryText
            // 마지막 알림 시간
            timestamp.text = info.recentNotiInfo.timestamp.toDateOrTime()

            // 안읽은 갯수
            info.unreadCnt.let {
                if (it > 0) {
                    unread.visibility = View.VISIBLE
                    unread.text = it.toString()
                } else {
                    unread.visibility = View.GONE
                }
            }

            // 노티 내용
            text.text = info.recentNotiInfo.text

            layout.setOnClickListener {
                listener(info.recentNotiInfo.pkgName, info.recentNotiInfo.summaryText)
            }
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): CategoryPageViewHolder {
            val binding = LayoutCategoryItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            return CategoryPageViewHolder(binding)
        }
    }
}