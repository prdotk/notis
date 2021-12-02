package com.inging.notis.ui.search

import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.constant.ClickMode
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.databinding.LayoutSearchRightItemBinding
import com.inging.notis.extension.getAppIcon
import com.inging.notis.extension.loadBitmap
import com.inging.notis.extension.searchWordHighlight
import com.inging.notis.extension.toDateTimeOrTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by annasu on 2021/04/26.
 */
class SearchRightViewHolder(
    private val binding: LayoutSearchRightItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        info: NotiInfo,
        listener: (Int, NotiInfo) -> Unit,
//        prevInfo: NotiInfo?,
//        nextInfo: NotiInfo?,
        word: String
    ) {
        binding.run {
//            val isSamePrevMin =
//                info.title == prevInfo?.title && info.senderType == prevInfo.senderType
//            val isSameNextMin = info.timestamp.checkSameMinute(nextInfo?.timestamp)
//                    && info.title == nextInfo?.title && info.senderType == nextInfo.senderType
//            val isDiffDay = info.timestamp.checkDiffDay(prevInfo?.timestamp)

//            if (isDiffDay) {
//                date.visibility = View.VISIBLE
//                date.text = info.timestamp.toDate(root.context)
//            } else {
//                date.visibility = View.GONE
//            }

//            if (isSamePrevMin && !isDiffDay) {
//                iconLayout.visibility = View.GONE
//                summary.visibility = View.GONE
//                title.visibility = View.GONE
//            } else {
//                iconLayout.visibility = View.VISIBLE
            summary.visibility = View.VISIBLE
            title.visibility = View.VISIBLE

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
            if (info.summaryText != info.title && info.summaryText != info.text) {
                summary.visibility = View.VISIBLE
                summary.text = info.summaryText
                summary.searchWordHighlight(word)
            } else {
                summary.visibility = View.GONE
            }
//            }

            // 노티 시간
            timestamp.text = info.timestamp.toDateTimeOrTime()
//            if (isSameNextMin) {
//                timestamp.visibility = View.INVISIBLE
//            } else {
            timestamp.visibility = View.VISIBLE
//            }

            // 노티 내용
            text.visibility = View.VISIBLE
            text.autoLinkMask = Linkify.ALL
            text.text = info.text
            text.searchWordHighlight(word)
            text.setOnClickListener {
                listener(ClickMode.MSG, info)
            }
            text.setOnLongClickListener {
                listener(ClickMode.LONG, info)
                true
            }

//            Linkify.addLinks(text, Linkify.ALL)
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): SearchRightViewHolder {
            val binding = LayoutSearchRightItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return SearchRightViewHolder(binding)
        }
    }
}