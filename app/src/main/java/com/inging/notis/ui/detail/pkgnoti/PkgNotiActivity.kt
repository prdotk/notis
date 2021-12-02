package com.inging.notis.ui.detail.pkgnoti

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isInvisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.inging.notis.R
import com.inging.notis.databinding.PkgNotiActivityBinding
import com.inging.notis.extension.getAppIcon
import com.inging.notis.extension.getAppName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PkgNotiActivity : AppCompatActivity() {

    private val viewModel: PkgNotiViewModel by viewModels()

    private lateinit var binding: PkgNotiActivityBinding

    private lateinit var fragment: PkgNotiFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.pkg_noti_activity)

        viewModel.pkgName = intent?.getStringExtra("PKG_NAME") ?: ""
        viewModel.word = intent?.getStringExtra("WORD") ?: ""
        viewModel.notiId = intent?.getLongExtra("NOTI_ID", -1) ?: -1

        fragment = PkgNotiFragment()

        // 앱 재기동 시 이미 생성된 프래그먼트 제거
        supportFragmentManager.fragments.forEach {
            supportFragmentManager.beginTransaction()
                .remove(it).commit()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commitNow()

        // 패키지 이름
        binding.title.text = getAppName(viewModel.pkgName)

        // 앱 아이콘
        lifecycleScope.launch(Dispatchers.Main) {
            binding.icon.setImageDrawable(getAppIcon(viewModel.pkgName))
        }

        // 뒤로가기
        binding.back.setOnClickListener {
            finish()
        }

        // 취소
        binding.cancel.setOnClickListener {
            fragment.finishEditMode()
        }

        // 삭제 버튼
        binding.delete.setOnClickListener {
            fragment.undoDelete()
        }

        viewModel.isEditMode.addOnPropertyChangedCallback(
            object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    if (sender is ObservableBoolean) {
                        val isEditMode = sender.get()
                        binding.menu.isInvisible = isEditMode
                        binding.cancel.isInvisible = !isEditMode
                        binding.delete.isInvisible = !isEditMode

                        fragment.setSwipe(!isEditMode)
                    }
                }
            })

        // 상단 메뉴 컨텍스트
        binding.menu.setOnClickListener { v ->
            PopupMenu(this, v).run {
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        // 편집
                        R.id.main_menu_edit -> {
                            viewModel.isEditMode.set(true)
                            true
                        }
                        // 모두 삭제
                        R.id.main_menu_delete_all -> {
                            deleteAll()
                            true
                        }
                        else -> false
                    }
                }
                menuInflater.inflate(R.menu.menu_msg_detail_context, menu)
                show()
            }
        }
    }

    override fun onBackPressed() {
        if (viewModel.isEditMode.get()) {
            fragment.finishEditMode()
        } else {
            super.onBackPressed()
        }
    }

    private fun deleteAll() {
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_all_notifications)
            .setPositiveButton(R.string.dialog_ok) { _, _ ->
                lifecycleScope.launch {
                    viewModel.deleteAll()
                    Snackbar.make(
                        binding.root,
                        R.string.snack_delete_all_done,
                        Snackbar.LENGTH_LONG
                    ).show()
                    finish()
                }
            }.setNegativeButton(R.string.dialog_cancel) { _, _ ->
            }.create().show()
    }
}