package com.inging.notis.ui.main.notification

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.inging.notis.R
import com.inging.notis.constant.ClickMode
import com.inging.notis.constant.NotiListMode
import com.inging.notis.constant.ServiceCommandType
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.databinding.MainNotificationFragmentBinding
import com.inging.notis.extension.loadNotiListMode
import com.inging.notis.extension.saveNotiListMode
import com.inging.notis.service.NotisNotificationListenerService
import com.inging.notis.ui.detail.pkgnoti.PkgNotiActivity
import com.inging.notis.ui.main.MainFragment
import com.inging.notis.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotiListFragment : MainFragment() {

    private val viewModel: NotiListViewModel by viewModels()

    private lateinit var binding: MainNotificationFragmentBinding

    private val itemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_notification_fragment, container, false)
        binding.lifecycleOwner = this

        // 검색
        binding.toolbar.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)
        }

        // 취소
        binding.cancel.setOnClickListener {
            lifecycleScope.launch {
                finishEditMode()
            }
        }

        // 리스트 전체 보기
        binding.listModeAll.setOnClickListener {
            viewModel.listMode.set(NotiListMode.PKG)
            requireContext().saveNotiListMode(NotiListMode.PKG)
        }

        // 리스트 앱별 보기
        binding.listModePkg.setOnClickListener {
            viewModel.listMode.set(NotiListMode.ALL)
            requireContext().saveNotiListMode(NotiListMode.ALL)
        }

        // 에디트 모드
        viewModel.isEditMode.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender is ObservableBoolean) {
                    val isEditMode = sender.get()
                    binding.listModeAll.isVisible =
                        !isEditMode && (viewModel.listMode.get() == NotiListMode.ALL)
                    binding.listModePkg.isVisible =
                        !isEditMode && (viewModel.listMode.get() == NotiListMode.PKG)
                    binding.menu.isVisible = !isEditMode
                    binding.cancel.isVisible = isEditMode
                    binding.delete.isVisible = isEditMode
                }
            }
        })

        // 리스트 모드
        viewModel.listMode.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender is ObservableInt) {
                    when (sender.get()) {
                        NotiListMode.ALL -> setupAllNotiList()
                        NotiListMode.PKG -> setupPkgNotiList()
                    }
                }
            }
        })

        // 리스트 모드 로드
        viewModel.listMode.set(requireContext().loadNotiListMode())

        // 삭제 버튼
        binding.delete.setOnClickListener {
            when (viewModel.listMode.get()) {
                NotiListMode.ALL -> delete()
                NotiListMode.PKG -> deletePkg()
            }
        }

        // 상단 메뉴 컨텍스트
        binding.menu.setOnClickListener { v ->
            PopupMenu(requireContext(), v).run {
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
                menuInflater.inflate(R.menu.menu_main_noti_context, menu)
                show()
            }
        }

        return binding.root
    }

    private fun setupAllNotiList() {
        binding.title.text = getText(R.string.title_noti)
        binding.listModeAll.visibility = View.VISIBLE
        binding.listModePkg.visibility = View.GONE

        val adapter = NotiListAllAdapter(
            viewModel.isEditMode,
            viewModel.deleteNotiList
        ) { mode, info, isChecked ->
            when (mode) {
                ClickMode.DEFAULT -> runContentIntent(info)
                ClickMode.CHECK -> // 체크 버튼 클릭 시 액션
                    if (isChecked) viewModel.deleteNotiList.add(info)
                    else viewModel.deleteNotiList.remove(info)
                ClickMode.LONG -> viewModel.isEditMode.set(true)
            }
        }

        lifecycleScope.launch {
            viewModel.allNotiList.collectLatest {
                adapter.submitData(lifecycle, it)
            }
        }

        binding.recycler.run {
//            itemAnimator = null
            this.adapter = adapter
            removeItemDecoration(itemDecoration)
            addItemDecoration(itemDecoration)
        }
    }

    private fun setupPkgNotiList() {
        binding.title.text = getText(R.string.title_noti_by_app)
        binding.listModeAll.visibility = View.GONE
        binding.listModePkg.visibility = View.VISIBLE

        val adapter = NotiListPkgAdapter(
            viewModel.isEditMode,
            viewModel.deletePkgList
        ) { mode, info, isChecked ->
            when (mode) {
                ClickMode.DEFAULT -> {
                    val intent = Intent(context, PkgNotiActivity::class.java)
                    intent.putExtra("PKG_NAME", info.pkgNameId)
                    startActivity(intent)
                }
                ClickMode.CHECK -> // 체크 버튼 클릭 시 액션
                    if (isChecked) viewModel.deletePkgList.add(info)
                    else viewModel.deletePkgList.remove(info)
                ClickMode.LONG -> viewModel.isEditMode.set(true)
            }
        }

        lifecycleScope.launch {
            viewModel.pkgNotiList.collectLatest {
                adapter.submitData(lifecycle, it)
            }
        }

        binding.recycler.run {
//            itemAnimator = null
            this.adapter = adapter
            removeItemDecoration(itemDecoration)
            addItemDecoration(itemDecoration)
        }
    }

    // 노티 액션 실행
    private fun runContentIntent(info: NotiInfo) {
        val intent = Intent(context, NotisNotificationListenerService::class.java)
        intent.putExtra("COMMAND_TYPE", ServiceCommandType.RUN_CONTENT_INTENT)
        intent.putExtra("PKG_NAME", info.pkgName)
        intent.putExtra("NOTI_ID", info.notiId)
        activity?.startService(intent)
    }

    override fun finishEditMode(): Boolean {
        return if (viewModel.isEditMode.get()) {
            viewModel.clearDeleteList()
            viewModel.isEditMode.set(false)
            true
        } else {
            false
        }
    }

    private fun delete() {
        lifecycleScope.launch {
            viewModel.deleteNoti()
            finishEditMode()
            Snackbar.make(
                binding.root,
                R.string.snack_selected_was_deleted,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun deletePkg() {
        lifecycleScope.launch {
            viewModel.deletePkgNoti()
            finishEditMode()
            Snackbar.make(
                binding.root,
                R.string.snack_selected_was_deleted,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun deleteAll() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.alert_delete_all)
            .setPositiveButton(R.string.alert_positive) { _, _ ->
                lifecycleScope.launch {
                    viewModel.deleteAll()
                    Snackbar.make(
                        binding.root,
                        R.string.snack_delete_all_done,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }.setNegativeButton(R.string.alert_negative) { _, _ ->
            }.create().show()
    }
}