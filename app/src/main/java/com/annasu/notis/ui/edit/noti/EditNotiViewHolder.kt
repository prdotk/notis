package com.annasu.notis.ui.edit.noti

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.annasu.notis.data.room.entity.NotiInfo
import com.annasu.notis.databinding.LayoutNotiLeftItemBinding
import com.annasu.notis.extension.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by datasaver on 2021/04/26.
 */
class EditNotiViewHolder(
    private val binding: LayoutNotiLeftItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        info: NotiInfo, listener: (Long, Boolean) -> Unit,
        prevInfo: NotiInfo?, nextInfo: NotiInfo?, deletedList: ObservableList<Long>, word: String
    ) {

        notiId = info.notiId

        val isSamePrevMin = info.timestamp.checkSameMinute(prevInfo?.timestamp)
                && info.title == prevInfo?.title && info.senderType == prevInfo.senderType
        val isSameNextMin = info.timestamp.checkSameMinute(nextInfo?.timestamp)
                && info.title == nextInfo?.title && info.senderType == nextInfo.senderType
        val isDiffDay = info.timestamp.checkDiffDay(nextInfo?.timestamp)

        binding.run {
            if (isDiffDay) {
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

            // 체크 박스
            check.visibility = View.VISIBLE
            updateChecked(deletedList)

            check.setOnClickListener {
                (it as? CheckBox)?.let { check ->
                    info.isChecked = check.isChecked
                    listener(info.notiId, check.isChecked)
                }
            }
            layout.setOnClickListener {
                check.performClick()
            }

            deletedList.addOnListChangedCallback(onSelectedItemsChanged)
        }
    }

    private var notiId = -1L

    private fun updateChecked(list: ObservableList<Long>?) {
        binding.run {
            val isChecked = list?.find {
                it == notiId
            }
            check.isChecked = isChecked != null
        }
    }

    private val onSelectedItemsChanged =
        object : ObservableList.OnListChangedCallback<ObservableList<Long>>() {
            override fun onChanged(sender: ObservableList<Long>?) {
                updateChecked(sender)
            }

            override fun onItemRangeChanged(
                sender: ObservableList<Long>?,
                positionStart: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }

            override fun onItemRangeInserted(
                sender: ObservableList<Long>?,
                positionStart: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }

            override fun onItemRangeMoved(
                sender: ObservableList<Long>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }

            override fun onItemRangeRemoved(
                sender: ObservableList<Long>?,
                positionStart: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }
        }

    companion object {
        fun getInstance(parent: ViewGroup): EditNotiViewHolder {
            val binding = LayoutNotiLeftItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return EditNotiViewHolder(binding)
        }
    }
}