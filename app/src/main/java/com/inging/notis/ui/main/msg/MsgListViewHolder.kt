package com.inging.notis.ui.main.msg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.databinding.Observable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.constant.ClickMode
import com.inging.notis.data.model.SimpleSummaryData
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.data.room.entity.SummaryInfo
import com.inging.notis.databinding.LayoutMsgListItemBinding
import com.inging.notis.extension.getAppIcon
import com.inging.notis.extension.loadBitmap
import com.inging.notis.extension.toDateOrTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by annasu on 2021/04/26.
 */
class MsgListViewHolder(
    private val binding: LayoutMsgListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var pkgName = ""
    private var summaryText = ""

    fun bind(
        info: SummaryInfo,
        isEditMode: ObservableBoolean,
        deletedList: ObservableArrayList<SimpleSummaryData>,
        listener: (Int, NotiInfo, Boolean) -> Unit
    ) {
        pkgName = info.recentNotiInfo.pkgName
        summaryText = info.recentNotiInfo.summaryText

        binding.run {
            // 앱 아이콘
            CoroutineScope(Dispatchers.Main).launch {
                val bitmap = info.recentNotiInfo.largeIcon.loadBitmap(root.context)
                if (bitmap != null) {
                    icon.visibility = View.INVISIBLE
                    largeIcon.visibility = View.VISIBLE
                    largeIcon.setImageBitmap(bitmap)
                    smallIcon.visibility = View.VISIBLE
//                    smallIcon.setImageDrawable(root.context.getAppIcon(info.recentNotiInfo.pkgName))
                } else {
                    icon.visibility = View.VISIBLE
                    icon.setImageDrawable(root.context.getAppIcon(info.recentNotiInfo.pkgName))
                    largeIcon.visibility = View.GONE
//                    smallIcon.visibility = View.GONE
                }
                smallIcon.setImageDrawable(root.context.getAppIcon(info.recentNotiInfo.pkgName))
            }
            // 타이틀
            title.text = info.recentNotiInfo.summaryText
            // 마지막 알림 시간
            timestamp.text = info.recentNotiInfo.timestamp.toDateOrTime()

            // 안읽은 갯수
            info.unreadCnt.let {
                if (it > 0) {
                    unread.visibility = View.VISIBLE
                    unread.text = it.toString()
                } else {
                    unread.visibility = View.GONE
                }
            }

            // 노티 내용
            text.text = info.recentNotiInfo.text

            // 체크 박스
            check.isVisible = isEditMode.get()
            updateChecked(deletedList)

            check.setOnClickListener {
                (it as? CheckBox)?.let { check ->
                    info.isChecked = check.isChecked
                    listener(ClickMode.CHECK, info.recentNotiInfo, check.isChecked)
                }
            }

            layout.setOnClickListener {
                if (isEditMode.get()) {
                    check.performClick()
                } else {
                    listener(ClickMode.DEFAULT, info.recentNotiInfo, false)
                }
            }

            layout.setOnLongClickListener {
                if (isEditMode.get()) {
                    check.performClick()
                } else {
                    listener(ClickMode.LONG, info.recentNotiInfo, false)
                }
                true
            }

            isEditMode.addOnPropertyChangedCallback(onEditModeChanged)

            deletedList.addOnListChangedCallback(onSelectedItemsChanged)
        }
    }

    private fun updateChecked(list: ObservableList<SimpleSummaryData>?) {
        binding.run {
            val isChecked = list?.find {
                it.pkgName == pkgName && it.summaryText == summaryText
            }
            check.isChecked = isChecked != null
        }
    }

    private val onEditModeChanged =
        object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender is ObservableBoolean) {
                    binding.check.isVisible = sender.get()
                }
            }
        }

    private val onSelectedItemsChanged =
        object : ObservableList.OnListChangedCallback<ObservableList<SimpleSummaryData>>() {
            override fun onChanged(sender: ObservableList<SimpleSummaryData>?) {
                updateChecked(sender)
            }

            override fun onItemRangeChanged(
                sender: ObservableList<SimpleSummaryData>?,
                positionStart: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }

            override fun onItemRangeInserted(
                sender: ObservableList<SimpleSummaryData>?,
                positionStart: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }

            override fun onItemRangeMoved(
                sender: ObservableList<SimpleSummaryData>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }

            override fun onItemRangeRemoved(
                sender: ObservableList<SimpleSummaryData>?,
                positionStart: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }
        }

    companion object {
        fun getInstance(parent: ViewGroup): MsgListViewHolder {
            val binding = LayoutMsgListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return MsgListViewHolder(binding)
        }
    }
}