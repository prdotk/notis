package com.inging.notis.ui.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by annasu on 2021/02/05.
 */
class LinearLayoutItemDecoration(
    private val topSpace: Int,
    private val bottomSpace: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val count = parent.adapter?.itemCount ?: 0

        when (position) {
            0 -> outRect.top = topSpace
            count - 1 -> outRect.bottom = bottomSpace
        }
    }
}