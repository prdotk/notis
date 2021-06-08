package com.annasu.notis.ui.main.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.annasu.notis.data.room.entity.PkgNotiInfo
import com.annasu.notis.databinding.LayoutNotificationPkgItemBinding
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
class NotificationPkgViewHolder(
    private val binding: LayoutNotificationPkgItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        info: PkgNotiInfo,
        listener: (Int, String, String, Boolean) -> Unit
    ) {
        binding.run {
            CoroutineScope(Dispatchers.Main).launch {
                // 앱 아이콘
                appIcon.setImageDrawable(root.context.getAppIcon(info.lastPkgNoti.pkgName))

                // 썸네일
                val bitmap = info.lastPkgNoti.largeIcon.loadBitmap(root.context)
                if (bitmap != null) {
                    thumbnail.visibility = View.VISIBLE
                    thumbnail.setImageBitmap(bitmap)
                } else {
                    thumbnail.visibility = View.GONE
                }
            }
            // 앱이름
            appName.text = root.context.getAppName(info.lastPkgNoti.pkgName)

            // 타이틀
            title.text = info.lastPkgNoti.summaryText

            // 마지막 알림 시간
            timestamp.text = info.lastPkgNoti.timestamp.toDateOrTime()

            // 노티 내용
            text.text = info.lastPkgNoti.text

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
        fun getInstance(parent: ViewGroup): NotificationPkgViewHolder {
            val binding = LayoutNotificationPkgItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return NotificationPkgViewHolder(binding)
        }
    }
}