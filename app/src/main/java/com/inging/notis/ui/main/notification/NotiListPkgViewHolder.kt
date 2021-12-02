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
import com.inging.notis.data.room.entity.PkgNotiInfo
import com.inging.notis.databinding.LayoutNotificationPkgItemBinding
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
class NotiListPkgViewHolder(
    private val binding: LayoutNotificationPkgItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var pkgNameId = ""

    fun bind(
        info: PkgNotiInfo,
        isEditMode: ObservableBoolean,
        deletedList: ObservableArrayList<PkgNotiInfo>,
        listener: (Int, PkgNotiInfo, Boolean) -> Unit
    ) {
        pkgNameId = info.pkgNameId

        binding.run {
            CoroutineScope(Dispatchers.Main).launch {
                // 앱 아이콘
                appIcon.setImageDrawable(root.context.getAppIcon(info.recentNotiInfo.pkgName))

                // 썸네일
                val bitmap = info.recentNotiInfo.largeIcon.loadBitmap(root.context)
                if (bitmap != null) {
                    thumbnail.visibility = View.VISIBLE
                    thumbnail.setImageBitmap(bitmap)
                } else {
                    thumbnail.visibility = View.GONE
                }
            }
            // 앱이름
            appName.text = root.context.getAppName(info.recentNotiInfo.pkgName)

            // 타이틀
            if (info.recentNotiInfo.title.isNotEmpty()) {
                title.visibility = View.VISIBLE
                title.text = info.recentNotiInfo.title
            } else {
                title.visibility = View.GONE
            }

            if (info.recentNotiInfo.title != info.recentNotiInfo.summaryText && info.recentNotiInfo.summaryText != info.recentNotiInfo.text) {
                summary.visibility = View.VISIBLE
                summary.text = info.recentNotiInfo.summaryText
            } else {
                summary.visibility = View.GONE
            }

            // 마지막 알림 시간
            timestamp.text = info.recentNotiInfo.timestamp.toDateTimeOrTime()

            // 노티 내용
            text.isVisible = info.recentNotiInfo.text.isNotEmpty()
            text.text = info.recentNotiInfo.text

            // 체크 박스
            check.isVisible = isEditMode.get()
            updateChecked(deletedList)

            check.setOnClickListener {
                (it as? CheckBox)?.let { check ->
                    info.recentNotiInfo.isChecked = check.isChecked
                    listener(ClickMode.CHECK, info, check.isChecked)
                }
            }

            layout.setOnClickListener {
                if (isEditMode.get()) {
                    check.performClick()
                } else {
                    listener(ClickMode.DEFAULT, info, false)
                }
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

    private fun updateChecked(list: ObservableList<PkgNotiInfo>?) {
        binding.run {
            val isChecked = list?.find {
                it.pkgNameId == pkgNameId
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
        object : ObservableList.OnListChangedCallback<ObservableList<PkgNotiInfo>>() {
            override fun onChanged(sender: ObservableList<PkgNotiInfo>?) {
                updateChecked(sender)
            }

            override fun onItemRangeChanged(
                sender: ObservableList<PkgNotiInfo>?,
                positionStart: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }

            override fun onItemRangeInserted(
                sender: ObservableList<PkgNotiInfo>?,
                positionStart: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }

            override fun onItemRangeMoved(
                sender: ObservableList<PkgNotiInfo>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }

            override fun onItemRangeRemoved(
                sender: ObservableList<PkgNotiInfo>?,
                positionStart: Int,
                itemCount: Int
            ) {
                updateChecked(sender)
            }
        }

    companion object {
        fun getInstance(parent: ViewGroup): NotiListPkgViewHolder {
            val binding = LayoutNotificationPkgItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return NotiListPkgViewHolder(binding)
        }
    }
}