package com.annasu.notis.ui.edit.noti

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.annasu.notis.data.room.entity.NotiInfo
import com.annasu.notis.databinding.LayoutNotiRightItemBinding
import com.annasu.notis.extension.*

/**
 * Created by datasaver on 2021/04/26.
 */
class EditNotiRightViewHolder(
    private val binding: LayoutNotiRightItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(info: NotiInfo, listener: (Long, Boolean) -> Unit,
             prevInfo: NotiInfo?, nextInfo: NotiInfo?, deletedList: ObservableList<Long>, word: String) {

        notiId = info.notiId

        val isSamePrevMin = info.timestamp.checkSameMinute(prevInfo?.timestamp)
            && info.title == prevInfo?.title && info.senderType == prevInfo.senderType
        val isDiffDay = info.timestamp.checkDiffDay(nextInfo?.timestamp)

        binding.run {
            if (isDiffDay) {
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

            override fun onItemRangeChanged(sender: ObservableList<Long>?, positionStart: Int, itemCount: Int) {
                updateChecked(sender)
            }

            override fun onItemRangeInserted(sender: ObservableList<Long>?, positionStart: Int, itemCount: Int) {
                updateChecked(sender)
            }

            override fun onItemRangeMoved(sender: ObservableList<Long>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                updateChecked(sender)
            }

            override fun onItemRangeRemoved(sender: ObservableList<Long>?, positionStart: Int, itemCount: Int) {
                updateChecked(sender)
            }
        }

    companion object {
        fun getInstance(parent: ViewGroup): EditNotiRightViewHolder {
            val binding = LayoutNotiRightItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            return EditNotiRightViewHolder(binding)
        }
    }
}