package com.annasu.notis.ui.detail

import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.databinding.Observable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableList
import com.annasu.notis.constant.ClickMode
import com.annasu.notis.data.room.entity.NotiInfo
import com.annasu.notis.databinding.LayoutMsgDetailRightItemBinding
import com.annasu.notis.extension.*

/**
 * Created by annasu on 2021/04/26.
 */
class MsgDetailRightViewHolder(
    private val binding: LayoutMsgDetailRightItemBinding
) : MsgDetailViewHolder(binding.root) {

    private val onEditModeChanged =
        object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender is ObservableBoolean) {
                    binding.check.isVisible = sender.get()
                }
            }
        }

    override fun bind(
        info: NotiInfo,
        prevInfo: NotiInfo?,
        nextInfo: NotiInfo?,
        word: String,
        lastNotiId: Long,
        isEditMode: ObservableBoolean,
        deletedList: ObservableArrayList<Long>,
        listener: (Int, Long, Boolean) -> Unit
    ) {
        notiId = info.notiId

        binding.run {
            val isSamePrevMin = info.timestamp.checkSameMinute(prevInfo?.timestamp)
                    && info.title == prevInfo?.title && info.senderType == prevInfo.senderType
            val isDiffDay = info.timestamp.checkDiffDay(nextInfo?.timestamp)

            if ((nextInfo != null && isDiffDay) ||
                (nextInfo == null && info.notiId == lastNotiId)
            ) {
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

            // 체크 박스
            check.isVisible = isEditMode.get()
            updateChecked(deletedList)

            check.setOnClickListener {
                (it as? CheckBox)?.let { check ->
                    info.isChecked = check.isChecked
                    listener(ClickMode.LONG, info.notiId, check.isChecked)
                }
            }

            layout.setOnClickListener {
                check.performClick()
            }

            layout.setOnLongClickListener {
                check.performClick()
                listener(ClickMode.LONG, info.notiId, false)
                true
            }

            deletedList.addOnListChangedCallback(onSelectedItemsChanged)

            isEditMode.addOnPropertyChangedCallback(onEditModeChanged)
        }
    }

    override fun updateChecked(list: ObservableList<Long>?) {
        binding.run {
            val isChecked = list?.find {
                it == notiId
            }
            check.isChecked = isChecked != null
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): MsgDetailRightViewHolder {
            val binding = LayoutMsgDetailRightItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return MsgDetailRightViewHolder(binding)
        }
    }
}