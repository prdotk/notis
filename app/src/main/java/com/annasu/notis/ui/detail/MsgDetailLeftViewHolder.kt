package com.annasu.notis.ui.detail

import android.annotation.SuppressLint
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.annasu.notis.databinding.LayoutMsgDetailLeftItemBinding
import com.annasu.notis.extension.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by annasu on 2021/04/26.
 */
class MsgDetailLeftViewHolder(
    private val binding: LayoutMsgDetailLeftItemBinding
) : MsgDetailViewHolder(binding.root) {

    // link 롱클릭 처리때문에 필요
    private var isLongClick = true

    // link 활성화 비활성화 때문에 저장함
    private var _text = ""

    @SuppressLint("ClickableViewAccessibility")
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
        _text = info.text

        binding.run {
            val isSamePrevMin = info.timestamp.checkSameMinute(prevInfo?.timestamp)
                    && info.title == prevInfo?.title && info.senderType == prevInfo.senderType
            val isSameNextMin = info.timestamp.checkSameMinute(nextInfo?.timestamp)
                    && info.title == nextInfo?.title && info.senderType == nextInfo.senderType
            val isDiffDay = info.timestamp.checkDiffDay(nextInfo?.timestamp)

            if ((nextInfo != null && isDiffDay) ||
                (nextInfo == null && info.notiId == lastNotiId)
            ) {
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
            text.autoLinkMask = Linkify.ALL
            text.text = info.text
            text.searchWordHighlight(word)

//            text.movementMethod = LinkMovementMethod.getInstance()
//            Linkify.addLinks(text, Linkify.ALL)

            text.isLongClickable = false

            text.setOnLongClickListener {
                isLongClick = true
                true
            }

            text.setOnClickListener {
                if (isEditMode.get()) {
                    check.performClick()
                }
            }

            text.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP && isLongClick) {
                    isLongClick = false
                    check.performClick()
                    listener(ClickMode.LONG, info.notiId, false)
                    return@setOnTouchListener true
                }
                if (event.action == MotionEvent.ACTION_DOWN) {
                    isLongClick = false
                }
                v.onTouchEvent(event)
            }

            // 체크 박스
            check.isVisible = isEditMode.get()
            updateChecked(deletedList)

            check.setOnClickListener {
                (it as? CheckBox)?.let { check ->
                    info.isChecked = check.isChecked
                    listener(ClickMode.CHECK, info.notiId, check.isChecked)
                }
            }

            layout.setOnClickListener {
                if (isEditMode.get()) {
                    check.performClick()
                }
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

    private val onEditModeChanged =
        object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender is ObservableBoolean) {
                    val isEditMode = sender.get()
                    if (isEditMode) {
                        binding.text.autoLinkMask = 0
                        binding.text.movementMethod = null
                        binding.text.text = _text
                    } else {
                        binding.text.autoLinkMask = Linkify.ALL
                        binding.text.text = _text
                    }
                    binding.check.isVisible = isEditMode
                }
            }
        }

    companion object {
        fun getInstance(parent: ViewGroup): MsgDetailLeftViewHolder {
            val binding = LayoutMsgDetailLeftItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return MsgDetailLeftViewHolder(binding)
        }
    }
}