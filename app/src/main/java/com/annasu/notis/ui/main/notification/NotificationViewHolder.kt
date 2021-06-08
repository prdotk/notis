package com.annasu.notis.ui.main.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.annasu.notis.data.room.entity.NotiInfo
import com.annasu.notis.databinding.LayoutNotificationItemBinding
import com.annasu.notis.extension.getAppIcon
import com.annasu.notis.extension.getAppName
import com.annasu.notis.extension.loadBitmap
import com.annasu.notis.extension.toDateOrTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by annasu on 2021/04/26.
 */
class NotificationViewHolder(
    private val binding: LayoutNotificationItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        info: NotiInfo,
        listener: (Int, String, String, Boolean) -> Unit
    ) {
        binding.run {
            CoroutineScope(Dispatchers.Main).launch {
                // 앱 아이콘
                appIcon.setImageDrawable(root.context.getAppIcon(info.pkgName))

                // 썸네일
                val bitmap = info.largeIcon.loadBitmap(root.context)
                if (bitmap != null) {
                    thumbnail.visibility = View.VISIBLE
                    thumbnail.setImageBitmap(bitmap)
                } else {
                    thumbnail.visibility = View.GONE
                }
            }
            // 앱이름
            appName.text = root.context.getAppName(info.pkgName)

            // 타이틀
            title.text = info.summaryText

            // 마지막 알림 시간
            timestamp.text = info.timestamp.toDateOrTime()

            // 노티 내용
            text.text = info.text

            // 클릭 시 앱으로 이동
            layout.setOnClickListener {
            }

            // 길게 클릭 시 메뉴
            layout.setOnLongClickListener {
                true
            }
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): NotificationViewHolder {
            val binding = LayoutNotificationItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return NotificationViewHolder(binding)
        }
    }
}