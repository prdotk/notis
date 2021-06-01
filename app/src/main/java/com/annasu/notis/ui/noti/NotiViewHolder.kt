package com.annasu.notis.ui.noti

import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.annasu.notis.data.room.entity.NotiInfo
import com.annasu.notis.databinding.LayoutNotiItemBinding
import com.annasu.notis.extension.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by datasaver on 2021/04/26.
 */
class NotiViewHolder(
    private val binding: LayoutNotiItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(info: NotiInfo, prevInfo: NotiInfo?, nextInfo: NotiInfo?,
             word: String, lastNotiId: Long) {
        binding.run {
            val isSamePrevMin = info.timestamp.checkSameMinute(prevInfo?.timestamp)
                && info.title == prevInfo?.title && info.senderType == prevInfo.senderType
            val isSameNextMin = info.timestamp.checkSameMinute(nextInfo?.timestamp)
                && info.title == nextInfo?.title && info.senderType == nextInfo.senderType
            val isDiffDay = info.timestamp.checkDiffDay(nextInfo?.timestamp)

            if ((nextInfo != null && isDiffDay) ||
                (nextInfo == null && info.notiId == lastNotiId)) {
                date.visibility = View.VISIBLE
                date.text = info.timestamp.toDate()
            } else {
                date.visibility = View.GONE
            }

            if (isSameNextMin) {
                icon.visibility = View.INVISIBLE
                largeIcon.visibility = View.GONE
                title.visibility = View.GONE
            } else {
                icon.visibility = View.VISIBLE
                largeIcon.visibility = View.VISIBLE
                title.visibility = View.VISIBLE
                // 앱 아이콘
                CoroutineScope(Dispatchers.Main).launch {
                    val bitmap = info.largeIcon.loadBitmap(root.context)
                    if (bitmap != null) {
                        icon.visibility = View.INVISIBLE
                        largeIcon.visibility = View.VISIBLE
                        largeIcon.setImageBitmap(bitmap)
                    } else {
                        icon.visibility = View.VISIBLE
                        largeIcon.visibility = View.GONE
                        icon.setImageDrawable(root.context.getAppIcon(info.pkgName))
                    }
                }
                // 노티 타이틀
                title.text = info.title
                title.searchWordHighlight(word)
            }

            // 노티 시간
            timestamp.text = info.timestamp.toTime()
            if (isSamePrevMin) {
                timestamp.visibility = View.INVISIBLE
            } else {
                timestamp.visibility = View.VISIBLE
            }

            // 노티 내용
            text.visibility = View.VISIBLE
            text.text = info.text
            text.searchWordHighlight(word)
            Linkify.addLinks(text, Linkify.ALL)
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): NotiViewHolder {
            val binding = LayoutNotiItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            return NotiViewHolder(binding)
        }
    }
}