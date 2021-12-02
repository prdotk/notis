package com.inging.notis.ui.detail.msg

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
import com.inging.notis.constant.ClickMode
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.databinding.LayoutMsgDetailRightItemBinding
import com.inging.notis.extension.checkSameMinute
import com.inging.notis.extension.searchWordHighlight
import com.inging.notis.extension.toTime

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
        isEditMode: ObservableBoolean,
        deletedList: ObservableArrayList<Long>,
        listener: (Int, NotiInfo, Boolean, Int) -> Unit,
        position: Int
    ) {
        notiId = info.notiId

        binding.run {
            val isSamePrevMin = info.timestamp.checkSameMinute(prevInfo?.timestamp)
                    && info.title == prevInfo?.title && info.senderType == prevInfo.senderType

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
                    listener(ClickMode.CHECK, info, check.isChecked, position)
                }
            }

            layout.setOnClickListener {
                if (isEditMode.get()) {
                    check.performClick()
                }
            }

            layout.setOnLongClickListener {
                if (isEditMode.get()) {
                    check.performClick()
                } else {
                    listener(ClickMode.LONG, info, false, position)
                }
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