package com.inging.notis.ui.search

import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.constant.ClickMode
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.databinding.LayoutSearchLeftItemBinding
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
class SearchLeftViewHolder(
    private val binding: LayoutSearchLeftItemBinding
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

                // ??? ?????????
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

                // ?????? ?????????
                title.text = info.title
                title.searchWordHighlight(word)

                // ?????? ?????????
                if (info.title != info.summaryText && info.summaryText != info.text) {
                    summary.visibility = View.VISIBLE
                    summary.text = info.summaryText
                    summary.searchWordHighlight(word)
                } else {
                    summary.visibility = View.GONE
                }
//            }

            // ?????? ??????
            timestamp.text = info.timestamp.toDateTimeOrTime()
//            if (isSameNextMin) {
//                timestamp.visibility = View.INVISIBLE
//            } else {
                timestamp.visibility = View.VISIBLE
//            }

            // ?????? ??????
            text.visibility = View.VISIBLE
            text.autoLinkMask = Linkify.ALL
//            text.setLinkTextColor(Color.WHITE)
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
        fun getInstance(parent: ViewGroup): SearchLeftViewHolder {
            val binding = LayoutSearchLeftItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return SearchLeftViewHolder(binding)
        }
    }
}