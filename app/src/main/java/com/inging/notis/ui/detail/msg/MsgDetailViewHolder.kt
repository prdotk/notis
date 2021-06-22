package com.inging.notis.ui.detail.msg

import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.data.room.entity.NotiInfo

open class MsgDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected var notiId = -1L

    protected val onSelectedItemsChanged =
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

    open fun bind(
        info: NotiInfo, prevInfo: NotiInfo?, nextInfo: NotiInfo?,
        word: String, lastNotiId: Long,
        isEditMode: ObservableBoolean,
        deletedList: ObservableArrayList<Long>,
        listener: (Int, NotiInfo, Boolean) -> Unit
    ) {
    }

    open fun updateChecked(list: ObservableList<Long>?) {}
}