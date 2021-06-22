package com.inging.notis.extension

import android.content.Context
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.inging.notis.R
import com.inging.notis.data.room.entity.NotiInfo

// 롱프레스 시 하단 메뉴
fun Context.showBottomSheetDialog(info: NotiInfo, deleteAction: () -> Unit) {
    BottomSheetDialog(this).run {
        setContentView(R.layout.layout_bottom_menu)
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

