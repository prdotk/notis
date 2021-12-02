package com.inging.notis.ui.main.notification

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
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.databinding.LayoutNotificationItemBinding
import com.inging.notis.extension.getAppIcon
import com.inging.notis.extension.getAppName
import com.inging.notis.extension.loadBitmap
import com.inging.notis.extension.toDateTimeOrTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by annasu on 2021/04/26.
 */
class NotiListAllViewHolder(
    private val binding: LayoutNotificationItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var notiId = -1L

    fun bind(
        info: NotiInfo,
        isEditMode: ObservableBoolean,
        deletedList: ObservableArrayList<NotiInfo>,
        listener: (Int, NotiInfo, Boolean) -> Unit
    ) {
        notiId = info.notiId

        binding.run {
            CoroutineScope(Dispatchers.Main).launch {
                // 앱 아이콘
                appIcon.setImageDrawable(root.context.getAppIcon(info.pkgName))

                // 썸네일
                thumbnail.visibility = View.GONE
                if (info.largeIcon.isNotEmpty()) {
                    val bitmap = info.largeIcon.loadBitmap(root.context)
                    if (bitmap != null) {
                        thumbnail.visibility = View.VISIBLE
                        thumbnail.setImageBitmap(bitmap)
                    }
                }

                picture.visibility = View.GONE
                if (info.picture.isNotEmpty()) {
                    val bitmap = info.picture.loadBitmap(root.context)
                    if (bitmap != null) {
                        picture.visibility = View.VISIBLE
                        picture.setImageBitmap(bitmap)
                    }
                }
            }

            // 앱이름
            appName.text = root.context.getAppName(info.pkgName)

            // 타이틀
            if (info.title.isNotEmpty()) {
                title.visibility = View.VISIBLE
                title.text = info.title
            } else {
                title.visibility = View.GONE
            }

            // 서머리
            if (info.summaryText != info.title && info.summaryText != info.text) {
                summary.visibility = View.VISIBLE
                summary.text = info.summaryText
            } else {
                summary.visibility = View.GONE
            }

            // 마지막 알림 시간
            timestamp.text = info.timestamp.toDateTimeOrTime()

            // 노티 내용
            text.isVisible = info.text.isNotEmpty()
            text.text = info.text

            // New 마크
//            markNew.isVisible = info.unread

            // 체크 박스
            check.isVisible = isEditMode.get()
            updateChecked(deletedList)

            check.setOnClickListener {
                (it as? CheckBox)?.let { check ->
                    info.isChecked = check.isChecked
                    listener(ClickMode.CHECK, info, check.isChecked)
                }
            }

            layout.setOnClickListener {
                if (isEditMode.get()) {
                    check.performClick()
                }
//                else {
//                    listener(ClickMode.DEFAULT, info, false)
//                }
            }

            layout.setOnLongClickListener {
                if (isEditMode.get()) {
                    check.performClick()
                } else {
                    listener(ClickMode.LONG, info, false)
                }
                true
            }

            isEditMode.addOnPropertyChangedCallback(onEditModeChanged)

            deletedList.addOnListChangedCallback(onSelectedItemsChanged)
        }
    }

    private fun updateChecked(list: ObservableList<NotiInfo>?) {
        binding.run {
            val isChecked = list?.find {
                it.notiId == notiId
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
        object : ObservableList.OnListChangedCallback<ObservableList<NotiInfo>>() {
            override fun onChanged(sender: ObservableList<NotiInfo>?) {
                updateChecked(sender)
            }

            override fun onItemRangeChanged(
                sender: ObservableList<NotiInfo>?,
                positionStart: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }

            override fun onItemRangeInserted(
                sender: ObservableList<NotiInfo>?,
                positionStart: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }

            override fun onItemRangeMoved(
                sender: ObservableList<NotiInfo>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }

            override fun onItemRangeRemoved(
                sender: ObservableList<NotiInfo>?,
                positionStart: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }
        }

    companion object {
        fun getInstance(parent: ViewGroup): NotiListAllViewHolder {
            val binding = LayoutNotificationItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return NotiListAllViewHolder(binding)
        }
    }
}