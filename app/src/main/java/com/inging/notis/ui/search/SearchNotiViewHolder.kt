package com.inging.notis.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.constant.ClickMode
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.databinding.LayoutNotificationItemBinding
import com.inging.notis.extension.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by annasu on 2021/04/26.
 */
class SearchNotiViewHolder(
    private val binding: LayoutNotificationItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var notiId = -1L

    fun bind(
        info: NotiInfo,
//        isEditMode: ObservableBoolean,
//        deletedList: ObservableArrayList<NotiInfo>,
        listener: (Int, NotiInfo) -> Unit,
        word: String
    ) {
        notiId = info.notiId

        binding.run {
            CoroutineScope(Dispatchers.Main).launch {
                // 앱 아이콘
                appIcon.setImageDrawable(root.context.getAppIcon(info.pkgName))

                // 썸네일
                val bitmap = info.largeIcon.loadBitmap(root.context)
                if (bitmap != null) {
                    thumbnail.visibility = View.VISIBLE
                    thumbnail.setImageBitmap(bitmap)
                } else {
                    thumbnail.visibility = View.GONE
                }
            }

            // 앱이름
            appName.text = root.context.getAppName(info.pkgName)

            // 타이틀
            title.text = info.title
            title.searchWordHighlight(word)

            // 서머리
            if (info.title != info.summaryText) {
                summary.visibility = View.VISIBLE
                summary.text = info.summaryText
                summary.searchWordHighlight(word)
            } else {
                summary.visibility = View.GONE
            }

            // 마지막 알림 시간
            timestamp.text = info.timestamp.toDateTimeOrTime()

            // 노티 내용
            text.text = info.text
            text.searchWordHighlight(word)

            // 체크 박스
//            check.isVisible = isEditMode.get()
//            updateChecked(deletedList)
//
//            check.setOnClickListener {
//                (it as? CheckBox)?.let { check ->
//                    info.isChecked = check.isChecked
//                    listener(ClickMode.CHECK, info, check.isChecked)
//                }
//            }

            layout.setOnClickListener {
//                if (isEditMode.get()) {
//                    check.performClick()
//                } else {
                    listener(ClickMode.NOTI, info)
//                }
            }

//            layout.setOnLongClickListener {
//                check.performClick()
//                listener(ClickMode.LONG, info, false)
//                true
//            }
//
//            isEditMode.addOnPropertyChangedCallback(onEditModeChanged)
//
//            deletedList.addOnListChangedCallback(onSelectedItemsChanged)
        }
    }

//    private fun updateChecked(list: ObservableList<NotiInfo>?) {
//        binding.run {
//            val isChecked = list?.find {
//                it.notiId == notiId
//            }
//            check.isChecked = isChecked != null
//        }
//    }
//
//    private val onEditModeChanged =
//        object : Observable.OnPropertyChangedCallback() {
//            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
//                if (sender is ObservableBoolean) {
//                    binding.check.isVisible = sender.get()
//                }
//            }
//        }
//
//    private val onSelectedItemsChanged =
//        object : ObservableList.OnListChangedCallback<ObservableList<NotiInfo>>() {
//            override fun onChanged(sender: ObservableList<NotiInfo>?) {
//                updateChecked(sender)
//            }
//
//            override fun onItemRangeChanged(
//                sender: ObservableList<NotiInfo>?,
//                positionStart: Int,
//                itemCount: Int
//            ) {
//                updateChecked(sender)
//            }
//
//            override fun onItemRangeInserted(
//                sender: ObservableList<NotiInfo>?,
//                positionStart: Int,
//                itemCount: Int
//            ) {
//                updateChecked(sender)
//            }
//
//            override fun onItemRangeMoved(
//                sender: ObservableList<NotiInfo>?,
//                fromPosition: Int,
//                toPosition: Int,
//                itemCount: Int
//            ) {
//                updateChecked(sender)
//            }
//
//            override fun onItemRangeRemoved(
//                sender: ObservableList<NotiInfo>?,
//                positionStart: Int,
//                itemCount: Int
//            ) {
//                updateChecked(sender)
//            }
//        }

    companion object {
        fun getInstance(parent: ViewGroup): SearchNotiViewHolder {
            val binding = LayoutNotificationItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return SearchNotiViewHolder(binding)
        }
    }
}