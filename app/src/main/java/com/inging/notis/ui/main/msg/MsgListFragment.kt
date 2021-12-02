package com.inging.notis.ui.main.msg

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.inging.notis.R
import com.inging.notis.constant.ClickMode
import com.inging.notis.data.model.SimpleSummaryData
import com.inging.notis.databinding.MainMessageFragmentBinding
import com.inging.notis.extension.showBottomSheetDialog
import com.inging.notis.ui.detail.msg.MsgDetailActivity
import com.inging.notis.ui.main.MainFragment
import com.inging.notis.ui.main.more.MoreActivity
import com.inging.notis.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MsgListFragment : MainFragment() {

    private val viewModel: MsgListViewModel by viewModels()

    private lateinit var binding: MainMessageFragmentBinding

    private var snack: Snackbar? = null

    private val _adapter: MsgListAdapter by lazy {
        MsgListAdapter(
            viewModel.isEditMode,
            viewModel.selectedList
        ) { mode, info, isChecked ->
            when (mode) {
                ClickMode.DEFAULT -> {
                    val intent = Intent(context, MsgDetailActivity::class.java)
                    intent.putExtra("PKG_NAME", info.pkgName)
                    intent.putExtra("SUMMARY_TEXT", info.summaryText)
                    startActivity(intent)
                }
                ClickMode.CHECK -> // 체크 버튼 클릭 시 액션
                    if (isChecked)
                        viewModel.selectedList.add(
                            SimpleSummaryData(info.pkgName, info.summaryText)
                        )
                    else
                        viewModel.selectedList.remove(
                            SimpleSummaryData(info.pkgName, info.summaryText)
                        )
                ClickMode.LONG -> //viewModel.isEditMode.set(true)
                    requireContext().showBottomSheetDialog(info) {
                        viewModel.selectedList.add(
                            SimpleSummaryData(info.pkgName, info.summaryText)
                        )
                        undoDelete()
//                        viewModel.isEditMode.set(true)
//                        viewModel.selectedList.add(
//                            SimpleSummaryData(info.pkgName, info.summaryText)
//                        )
                    }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_message_fragment, container, false)
        binding.lifecycleOwner = this

        lifecycleScope.launch {
            viewModel.messageList.collectLatest {
                _adapter.submitData(lifecycle, it)
            }
        }

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

        binding.recycler.run {
//            itemAnimator = null
            adapter = _adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        viewModel.isEditMode.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender is ObservableBoolean) {
                    val isEditMode = sender.get()
                    binding.menu.isVisible = !isEditMode
                    binding.cancel.isVisible = isEditMode
                    binding.delete.isVisible = isEditMode
                }
            }
        })

        binding.delete.setOnClickListener {
            undoDelete()
        }

        // 상단 메뉴 컨텍스트
        binding.menu.setOnClickListener { v ->
//            val location = IntArray(2)
//            v.getLocationOnScreen(location)
//            val point = Point().apply {
//                x = location[0]
//                y = location[1]
//            }
//            requireActivity().showPopupMenu(point)
            PopupMenu(requireContext(), v).run {
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        // 모두 읽음 처리
                        R.id.main_menu_read_all -> {
                            readAll()
                            true
                        }
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
                        // 설정
                        R.id.main_menu_settings -> {
                            startActivity(Intent(context, MoreActivity::class.java))
                            true
                        }
                        else -> false
                    }
                }
                menuInflater.inflate(R.menu.menu_main_msg_context, menu)
                show()
            }
        }

        return binding.root
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {

    }

    override fun finishEditMode(): Boolean {
        return if (viewModel.isEditMode.get()) {
            viewModel.isEditMode.set(false)
            viewModel.selectedList.clear()
            true
        } else {
            false
        }
    }

    private fun undoDelete() {
        lifecycleScope.launch {
            snack?.dismiss()

            viewModel.deleteList = viewModel.selectedList.toList()
            viewModel.undoDelete()

            finishEditMode()

            val message = "${viewModel.deleteList.size} ${getString(R.string.snack_deleted)}"
            snack = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).run {
                setAction(getString(R.string.snack_undo)) {
                    lifecycleScope.launch {
                        viewModel.undoRestore()
                        viewModel.selectedList.clear()
                    }
                }
                addCallback(object : Snackbar.Callback() {
                    val list = viewModel.deleteList.toList()
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        when (event) {
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION -> {
                            }
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_CONSECUTIVE,
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_MANUAL,
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE,
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT -> {
                                viewModel.delete(list)
                            }
                        }
                    }
                })
            }
            snack?.show()
        }
    }

    private fun deleteAll() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.delete_all_messages)
            .setPositiveButton(R.string.dialog_ok) { _, _ ->
                lifecycleScope.launch {
                    viewModel.deleteAll()
                    Snackbar.make(
                        binding.root,
                        R.string.snack_delete_all_done,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }.setNegativeButton(R.string.dialog_cancel) { _, _ ->
            }.create().show()
    }

    private fun readAll() {
        lifecycleScope.launch {
            viewModel.readAll()
            Snackbar.make(
                binding.root,
                R.string.snack_read_all_done,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}