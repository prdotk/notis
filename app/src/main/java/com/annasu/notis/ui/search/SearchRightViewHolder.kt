package com.annasu.notis.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.annasu.notis.data.room.entity.NotiInfo
import com.annasu.notis.databinding.LayoutSearchRightItemBinding
import com.annasu.notis.extension.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by datasaver on 2021/04/26.
 */
class SearchRightViewHolder(
    private val binding: LayoutSearchRightItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(info: NotiInfo, listener: (String, String, Long) -> Unit,
             prevInfo: NotiInfo?, nextInfo: NotiInfo?, word: String) {
        binding.run {
            val isSamePrevMin =
                info.title == prevInfo?.title && info.senderType == prevInfo.senderType
            val isSameNextMin = info.timestamp.checkSameMinute(nextInfo?.timestamp)
                && info.title == nextInfo?.title && info.senderType == nextInfo.senderType
//            val isSamePrevMin = info.timestamp.checkSameMinute(prevInfo?.timestamp)
//                && info.title == prevInfo?.title && info.senderType == prevInfo.senderType
//            val isSameNextMin =
//                info.title == nextInfo?.title && info.senderType == nextInfo.senderType
            val isDiffDay = info.timestamp.checkDiffDay(prevInfo?.timestamp)

            if (isDiffDay) {
                date.visibility = View.VISIBLE
                date.text = info.timestamp.toDate()
            } else {
                date.visibility = View.GONE
            }

            if (isSamePrevMin && !isDiffDay) {
//            if (isSameNextMin && !isDiffDay) {
                iconLayout.visibility = View.GONE
                titleLayout.visibility = View.GONE
            } else {
                iconLayout.visibility = View.VISIBLE
                titleLayout.visibility = View.VISIBLE

                // 앱 아이콘
                CoroutineScope(Dispatchers.Main).launch {
                    val bitmap = info.largeIcon.loadBitmap(root.context)
                    if (bitmap != null) {
                        icon.visibility = View.INVISIBLE
                        largeIcon.visibility = View.VISIBLE
                        largeIcon.setImageBitmap(bitmap)
                        smallIcon.visibility = View.VISIBLE
                        smallIcon.setImageDrawable(root.context.getAppIcon(info.pkgName))
                    } else {
                        icon.visibility = View.VISIBLE
                        icon.setImageDrawable(root.context.getAppIcon(info.pkgName))
                        largeIcon.visibility = View.GONE
                        smallIcon.visibility = View.INVISIBLE
                    }
                }

                // 노티 타이틀
                title.text = info.title
                title.searchWordHighlight(word)

                // 노티 서머리
                if (info.summaryText.isEmpty() || info.title == info.summaryText) {
                    summary.visibility = View.GONE
                } else {
                    summary.visibility = View.VISIBLE
                    summary.text = info.summaryText
                    summary.searchWordHighlight(word)
                }
            }

            // 노티 시간
            timestamp.text = info.timestamp.toTime()
            if (isSameNextMin) {
//            if (isSamePrevMin) {
                timestamp.visibility = View.INVISIBLE
            } else {
                timestamp.visibility = View.VISIBLE
            }

            // 노티 내용
            text.text = info.text
            text.searchWordHighlight(word)
            text.setOnClickListener {
                listener(info.pkgName, info.summaryText, info.notiId)
            }
//            Linkify.addLinks(text, Linkify.ALL)
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): SearchRightViewHolder {
            val binding = LayoutSearchRightItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            return SearchRightViewHolder(binding)
        }
    }
}