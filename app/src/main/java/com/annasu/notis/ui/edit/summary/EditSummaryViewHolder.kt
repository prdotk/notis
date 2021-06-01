package com.annasu.notis.ui.edit.summary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.annasu.notis.data.model.SimpleSummaryData
import com.annasu.notis.data.room.entity.SummaryInfo
import com.annasu.notis.databinding.LayoutCategoryItemBinding
import com.annasu.notis.extension.getAppIcon
import com.annasu.notis.extension.loadBitmap
import com.annasu.notis.extension.toDateOrTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by datasaver on 2021/04/26.
 */
class EditSummaryViewHolder(
    private val binding: LayoutCategoryItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(info: SummaryInfo, listener: (String, String, Boolean) -> Unit,
             deletedList: ObservableArrayList<SimpleSummaryData>) {

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
                    smallIcon.setImageDrawable(root.context.getAppIcon(info.recentNotiInfo.pkgName))
                } else {
                    icon.visibility = View.VISIBLE
                    icon.setImageDrawable(root.context.getAppIcon(info.recentNotiInfo.pkgName))
                    largeIcon.visibility = View.GONE
                    smallIcon.visibility = View.GONE
                }
            }

            // 타이틀
            title.text = info.recentNotiInfo.summaryText

            // 마지막 알림 시간
            timestamp.text = info.recentNotiInfo.timestamp.toDateOrTime()

            // 노티 내용
            text.text = info.recentNotiInfo.text

            // 체크 박스
            check.visibility = View.VISIBLE
            updateChecked(deletedList)

            check.setOnClickListener {
                (it as? CheckBox)?.let { check ->
                    info.isChecked = check.isChecked
                    listener(info.recentNotiInfo.pkgName, info.recentNotiInfo.summaryText, check.isChecked)
                }
            }

            layout.setOnClickListener {
                check.performClick()
            }

            deletedList.addOnListChangedCallback(onSelectedItemsChanged)
        }
    }

    private var pkgName = ""
    private var summaryText = ""

    private fun updateChecked(list: ObservableList<SimpleSummaryData>?) {
        binding.run {
            val isChecked = list?.find {
                it.pkgName == pkgName && it.summaryText == summaryText
            }
            check.isChecked = isChecked != null
        }
    }

    private val onSelectedItemsChanged =
        object : ObservableList.OnListChangedCallback<ObservableList<SimpleSummaryData>>() {
            override fun onChanged(sender: ObservableList<SimpleSummaryData>?) {
                updateChecked(sender)
            }

            override fun onItemRangeChanged(sender: ObservableList<SimpleSummaryData>?, positionStart: Int, itemCount: Int) {
                updateChecked(sender)
            }

            override fun onItemRangeInserted(sender: ObservableList<SimpleSummaryData>?, positionStart: Int, itemCount: Int) {
                updateChecked(sender)
            }

            override fun onItemRangeMoved(sender: ObservableList<SimpleSummaryData>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                updateChecked(sender)
            }

            override fun onItemRangeRemoved(sender: ObservableList<SimpleSummaryData>?, positionStart: Int, itemCount: Int) {
                updateChecked(sender)
            }
        }

    companion object {
        fun getInstance(parent: ViewGroup): EditSummaryViewHolder {
            val binding = LayoutCategoryItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            return EditSummaryViewHolder(binding)
        }
    }
}