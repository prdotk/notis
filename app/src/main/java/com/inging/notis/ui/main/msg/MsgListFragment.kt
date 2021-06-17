package com.inging.notis.ui.main.msg

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.inging.notis.R
import com.inging.notis.constant.ClickMode
import com.inging.notis.data.model.SimpleSummaryData
import com.inging.notis.databinding.MainMessageFragmentBinding
import com.inging.notis.ui.detail.msg.MsgDetailActivity
import com.inging.notis.ui.main.MainFragment
import com.inging.notis.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MsgListFragment : MainFragment() {

    private val viewModel: MsgListViewModel by viewModels()

    private lateinit var binding: MainMessageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_message_fragment, container, false)
        binding.lifecycleOwner = this

        val adapter = MsgListAdapter(
            viewModel.isEditMode,
            viewModel.deleteList
        ) { mode, pkgName, summaryText, isChecked ->
            when (mode) {
                ClickMode.DEFAULT -> {
                    val intent = Intent(context, MsgDetailActivity::class.java)
                    intent.putExtra("PKG_NAME", pkgName)
                    intent.putExtra("SUMMARY_TEXT", summaryText)
                    startActivity(intent)
                }
                ClickMode.CHECK -> // 체크 버튼 클릭 시 액션
                    if (isChecked) viewModel.deleteList.add(SimpleSummaryData(pkgName, summaryText))
                    else viewModel.deleteList.remove(SimpleSummaryData(pkgName, summaryText))
                ClickMode.LONG -> viewModel.isEditMode.set(true)
            }
        }

        lifecycleScope.launch {
            viewModel.messageList.collectLatest {
                adapter.submitData(lifecycle, it)
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
            this.adapter = adapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        viewModel.isEditMode.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender is ObservableBoolean) {
                    val isEditMode = sender.get()
                    binding.menu.isVisible = !isEditMode
                    binding.cancel.isVisible = isEditMode
                    binding.delete.isVisible = isEditMode
//                    binding.fabRead.isVisible = !isEditMode
//                    binding.fabDelete.isVisible = isEditMode
                }
            }
        })

        binding.delete.setOnClickListener {
            delete()
        }

        // 상단 메뉴 컨텍스트
        binding.menu.setOnClickListener { v ->
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
                        else -> false
                    }
                }
                menuInflater.inflate(R.menu.menu_main_msg_context, menu)
                show()
            }
        }
        return binding.root
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
            viewModel.delete()
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