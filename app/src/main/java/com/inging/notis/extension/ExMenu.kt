package com.inging.notis.extension

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.inging.notis.R
import com.inging.notis.data.room.entity.NotiInfo


// 롱프레스 시 하단 메뉴 (메시지)
fun Context.showBottomSheetDialog(info: NotiInfo, deleteAction: () -> Unit) {
    BottomSheetDialog(this).run {
        setContentView(R.layout.layout_menu_bottom_sheet)
        // 앱 열기
        findViewById<LinearLayout>(R.id.open)?.setOnClickListener {
            this@showBottomSheetDialog.runContentIntent(info)
            dismiss()
        }
        // 클립보드에 복사
        findViewById<LinearLayout>(R.id.copy)?.setOnClickListener {
            this@showBottomSheetDialog.copyToClipboard("${info.summaryText}\n\n${info.text}")
            Toast.makeText(
                this@showBottomSheetDialog,
                R.string.snack_copied_to_clipboard,
                Toast.LENGTH_LONG
            ).show()
            dismiss()
        }
        // 삭제
        findViewById<LinearLayout>(R.id.delete)?.setOnClickListener {
            deleteAction()
            dismiss()
        }
        show()
    }
}

fun Activity.showPopupMenu(p: Point) {

    // Inflate the popup_layout.xml
    val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout: View = layoutInflater.inflate(R.layout.layout_menu_popup, null)
    val viewGroup = layout.findViewById<View>(R.id.popupLayout) as LinearLayout

    // Creating the PopupWindow
    val changeStatusPopUp = PopupWindow(this)
    changeStatusPopUp.contentView = layout
    changeStatusPopUp.width = LinearLayout.LayoutParams.WRAP_CONTENT
    changeStatusPopUp.height = LinearLayout.LayoutParams.WRAP_CONTENT
    changeStatusPopUp.isFocusable = true

    // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
    val offsetX = -20
    val offsetY = -20

    //Clear the default translucent background
    changeStatusPopUp.setBackgroundDrawable(BitmapDrawable())

    // Displaying the popup at the specified location, + offsets.
    changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + offsetX, p.y + offsetY)
}