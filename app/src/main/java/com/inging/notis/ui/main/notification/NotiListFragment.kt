package com.inging.notis.ui.main.notification

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isInvisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.inging.notis.R
import com.inging.notis.constant.ClickMode
import com.inging.notis.constant.NotiListMode
import com.inging.notis.databinding.MainNotificationFragmentBinding
import com.inging.notis.extension.loadNotiListMode
import com.inging.notis.extension.runContentIntent
import com.inging.notis.extension.saveNotiListMode
import com.inging.notis.extension.showBottomSheetDialog
import com.inging.notis.ui.detail.pkgnoti.PkgNotiActivity
import com.inging.notis.ui.main.MainFragment
import com.inging.notis.ui.main.more.MoreActivity
import com.inging.notis.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NotiListFragment : MainFragment() {

    private val viewModel: NotiListViewModel by viewModels()

    private lateinit var binding: MainNotificationFragmentBinding

    private var snack: Snackbar? = null

    private val itemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    }

    // 모든 노티 리스트 어댑터
    private val allAdapter: NotiListAllAdapter by lazy {
        NotiListAllAdapter(
            viewModel.isEditMode,
            viewModel.selectedNotiList
        )
        { mode, info, isChecked ->
            when (mode) {
                ClickMode.DEFAULT -> { context?.runContentIntent(info) }
                ClickMode.CHECK -> // 체크 버튼 클릭 시 액션
                    if (isChecked) viewModel.selectedNotiList.add(info)
                    else viewModel.selectedNotiList.remove(info)
                ClickMode.LONG -> //viewModel.isEditMode.set(true)
                    requireContext().showBottomSheetDialog(info) {
                        viewModel.selectedNotiList.add(info)
                        undoDeleteNoti()
                    }
            }
        }
    }

    // 패키지 노티 리스트 어댑터
    private val pkgAdapter: NotiListPkgAdapter by lazy {
        NotiListPkgAdapter(
            viewModel.isEditMode,
            viewModel.selectedPkgList
        ) { mode, info, isChecked ->
            when (mode) {
                ClickMode.DEFAULT -> {
                    val intent = Intent(context, PkgNotiActivity::class.java)
                    intent.putExtra("PKG_NAME", info.pkgNameId)
                    startActivity(intent)
                }
                ClickMode.CHECK -> // 체크 버튼 클릭 시 액션
                    if (isChecked) viewModel.selectedPkgList.add(info)
                    else viewModel.selectedPkgList.remove(info)
                ClickMode.LONG -> //viewModel.isEditMode.set(true)
                    requireContext().showBottomSheetDialog(info.recentNotiInfo) {
                        viewModel.selectedPkgList.add(info)
                        undoDeletePkg()
                    }
            }
        }
    }

    private val allItemTouchHelper = ItemTouchHelper(object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            allAdapter.peek(viewHolder.bindingAdapterPosition)?.let {
                viewModel.selectedNotiList.add(it)
                undoDeleteNoti()
//                lifecycleScope.launch {
//                    viewModel.deleteNoti(it)
//                }
            }
        }
    })

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
                    binding.listModeAll.isInvisible =
                        !(!isEditMode && (viewModel.listMode.get() == NotiListMode.ALL))
                    binding.listModePkg.isInvisible =
                        !(!isEditMode && (viewModel.listMode.get() == NotiListMode.PKG))
                    binding.menu.isInvisible = isEditMode
                    binding.cancel.isInvisible = !isEditMode
                    binding.delete.isInvisible = !isEditMode

                    setSwipe(!isEditMode)
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
                NotiListMode.ALL -> undoDeleteNoti()
                NotiListMode.PKG -> undoDeletePkg()
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
                        // 설정
                        R.id.main_menu_settings -> {
                            startActivity(Intent(context, MoreActivity::class.java))
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

        lifecycleScope.launch {
            viewModel.allNotiList.collectLatest {
                allAdapter.submitData(lifecycle, it)
            }
        }

        binding.recycler.run {
//            itemAnimator = null
            adapter = allAdapter
            removeItemDecoration(itemDecoration)
//            addItemDecoration(itemDecoration)
        }

        setSwipe(true)
    }

    private fun setupPkgNotiList() {
        binding.title.text = getText(R.string.title_noti_by_app)
        binding.listModeAll.visibility = View.GONE
        binding.listModePkg.visibility = View.VISIBLE

        lifecycleScope.launch {
            viewModel.pkgNotiList.collectLatest {
                pkgAdapter.submitData(lifecycle, it)
            }
        }

        binding.recycler.run {
//            itemAnimator = null
            adapter = pkgAdapter
            removeItemDecoration(itemDecoration)
            addItemDecoration(itemDecoration)
        }

        setSwipe(false)
    }

    private fun setSwipe(enable: Boolean) {
        if (enable) {
            allItemTouchHelper.attachToRecyclerView(binding.recycler)
        } else {
            allItemTouchHelper.attachToRecyclerView(null)
        }
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

    private fun undoDeleteNoti() {
        lifecycleScope.launch {
            snack?.dismiss()

            viewModel.deleteNotiList = viewModel.selectedNotiList.toList()
            viewModel.undoDeleteNoti()

            finishEditMode()

            val message = "${viewModel.deleteNotiList.size} ${getString(R.string.snack_deleted)}"
            snack = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).run {
                setAction(getString(R.string.snack_undo)) {
                    lifecycleScope.launch {
                        viewModel.undoRestoreNoti()
                        viewModel.selectedNotiList.clear()
                    }
                }
                addCallback(object : Snackbar.Callback() {
                    val list = viewModel.deleteNotiList.toList()
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        when (event) {
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION -> {
                            }
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_CONSECUTIVE,
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_MANUAL,
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE,
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT -> {
                                viewModel.deleteNoti(list)
                            }
                        }
                    }
                })
            }
            snack?.show()
        }
    }

    private fun undoDeletePkg() {
        lifecycleScope.launch {
            snack?.dismiss()

            viewModel.deletePkgList = viewModel.selectedPkgList.toList()
            viewModel.undoDeletePkg()

            finishEditMode()

            val message = "${viewModel.deletePkgList.size} ${getString(R.string.snack_deleted)}"
            snack = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).run {
                setAction(getString(R.string.snack_undo)) {
                    lifecycleScope.launch {
                        viewModel.undoRestorePkg()
                        viewModel.selectedPkgList.clear()
                    }
                }
                addCallback(object : Snackbar.Callback() {
                    val list = viewModel.deletePkgList.toList()
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        when (event) {
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION -> {
                            }
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_CONSECUTIVE,
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_MANUAL,
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE,
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT -> {
                                viewModel.deletePkg(list)
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
            .setMessage(R.string.delete_all_notifications)
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
}