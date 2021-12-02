package com.inging.notis.ui.detail.pkgnoti

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.inging.notis.R
import com.inging.notis.constant.ClickMode
import com.inging.notis.constant.ServiceCommandType
import com.inging.notis.databinding.PkgNotiFragmentBinding
import com.inging.notis.extension.showBottomSheetDialog
import com.inging.notis.service.NotisNotificationListenerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PkgNotiFragment : Fragment() {

    private val viewModel: PkgNotiViewModel by activityViewModels()

    private lateinit var binding: PkgNotiFragmentBinding

    private var firstLoad = true

//    private val itemDecoration: DividerItemDecoration by lazy {
//        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//    }

    private var snack: Snackbar? = null

    private val _adapter: PkgNotiAllAdapter by lazy {
        PkgNotiAllAdapter(
            viewModel.word,
            viewModel.isEditMode,
            viewModel.selectedList
        ) { mode, info, isChecked ->
            when (mode) {
                ClickMode.DEFAULT ->
                    Intent(context, NotisNotificationListenerService::class.java).run {
                        putExtra("COMMAND_TYPE", ServiceCommandType.RUN_CONTENT_INTENT)
                        putExtra("PKG_NAME", info.pkgName)
                        putExtra("NOTI_ID", info.notiId)
                        activity?.startService(this)
                    }
                ClickMode.CHECK -> // 체크 버튼 클릭 시 액션
                    if (isChecked) viewModel.selectedList.add(info)
                    else viewModel.selectedList.remove(info)
                ClickMode.LONG -> //viewModel.isEditMode.set(true)
                    requireContext().showBottomSheetDialog(info) {
                        viewModel.selectedList.add(info)
                        undoDelete()
                    }
            }
        }
    }

    private val _itemTouchHelper = ItemTouchHelper(object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            _adapter.peek(viewHolder.bindingAdapterPosition)?.let {
                viewModel.selectedList.add(it)
                undoDelete()
            }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.pkg_noti_fragment, container, false)
        binding.lifecycleOwner = this

        lifecycleScope.launch {
            viewModel.pkgNotiList(viewModel.pkgName).collectLatest {
                _adapter.submitData(lifecycle, it)
                lifecycleScope.launch {
                    delay(100)
                    if (firstLoad) {
                        firstLoad = false
//                        binding.recycler.scrollToPosition(viewModel.findPosition())
                        binding.recycler.smoothScrollToPosition(viewModel.findPosition())
                    }
//                    else {
//                        val firstVisible = (binding.recycler.layoutManager as LinearLayoutManager)
//                            .findFirstVisibleItemPosition()
//                        if (firstVisible <= 1) {
//                            binding.recycler.scrollToPosition(0)
//                        }
//                        _adapter.notifyItemChanged(1)
//                    }
                }
            }
        }

        binding.recycler.run {
//            itemAnimator = null
            adapter = _adapter
//            removeItemDecoration(itemDecoration)
//            addItemDecoration(itemDecoration)
        }

        setSwipe(true)

        return binding.root
    }

    fun undoDelete() {
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

    fun finishEditMode() {
        lifecycleScope.launch {
            viewModel.clearDeleteList()
            viewModel.isEditMode.set(false)
        }
    }

    fun setSwipe(enable: Boolean) {
        if (enable) {
            _itemTouchHelper.attachToRecyclerView(binding.recycler)
        } else {
            _itemTouchHelper.attachToRecyclerView(null)
        }
    }
}