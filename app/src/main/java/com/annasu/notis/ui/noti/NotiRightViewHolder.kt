package com.annasu.notis.ui.noti

import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.annasu.notis.data.room.entity.NotiInfo
import com.annasu.notis.databinding.LayoutNotiRightItemBinding
import com.annasu.notis.extension.*

/**
 * Created by datasaver on 2021/04/26.
 */
class NotiRightViewHolder(
    private val binding: LayoutNotiRightItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(info: NotiInfo, prevInfo: NotiInfo?, nextInfo: NotiInfo?,
             word: String, lastNotiId: Long) {
        binding.run {
            val isSamePrevMin = info.timestamp.checkSameMinute(prevInfo?.timestamp)
                && info.title == prevInfo?.title && info.senderType == prevInfo.senderType
            val isDiffDay = info.timestamp.checkDiffDay(nextInfo?.timestamp)

            if ((nextInfo != null && isDiffDay) ||
                (nextInfo == null && info.notiId == lastNotiId)) {
                date.visibility = View.VISIBLE
                date.text = info.timestamp.toDate()
            } else {
                date.visibility = View.GONE
            }

            // 노티 시간
            timestamp.text = info.timestamp.toTime()
            if (isSamePrevMin) {
                timestamp.visibility = View.INVISIBLE
            } else {
                timestamp.visibility = View.VISIBLE
            }

            // 노티 내용
            text.text = info.text
            text.searchWordHighlight(word)
            Linkify.addLinks(text, Linkify.ALL)
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): NotiRightViewHolder {
            val binding = LayoutNotiRightItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            return NotiRightViewHolder(binding)
        }
    }
}