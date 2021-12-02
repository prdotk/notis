package com.inging.notis.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.inging.notis.R
import com.inging.notis.constant.ClickMode
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.databinding.SearchFragmentBinding
import com.inging.notis.extension.dp2Pixel
import com.inging.notis.extension.showBottomSheetDialog
import com.inging.notis.ui.custom.LinearLayoutItemDecoration
import com.inging.notis.ui.detail.msg.MsgDetailActivity
import com.inging.notis.ui.detail.pkgnoti.PkgNotiActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by activityViewModels()

    private lateinit var binding: SearchFragmentBinding

    private var oldJob: Job? = null

    private var snack: Snackbar? = null

    private val _adapter: SearchAdapter by lazy {
        SearchAdapter { mode, info ->
            when (mode) {
                ClickMode.MSG -> Intent(context, MsgDetailActivity::class.java).run {
                    putExtra("PKG_NAME", info.pkgName)
                    putExtra("SUMMARY_TEXT", info.summaryText)
                    putExtra("WORD", viewModel.word)
                    putExtra("NOTI_ID", info.notiId)
                    startActivity(this)
                }
                ClickMode.NOTI -> Intent(context, PkgNotiActivity::class.java).run {
                    putExtra("PKG_NAME", info.pkgName)
                    putExtra("WORD", viewModel.word)
                    putExtra("NOTI_ID", info.notiId)
                    startActivity(this)
                }
                ClickMode.LONG -> requireContext().showBottomSheetDialog(info) {
                    undoDelete(info)
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
                undoDelete(it)
            }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false)
        binding.lifecycleOwner = this

        binding.recycler.run {
//            itemAnimator = null
            adapter = _adapter
            addItemDecoration(
                LinearLayoutItemDecoration(
                    0,
                    requireContext().dp2Pixel(25f)
                )
            )
        }

        _itemTouchHelper.attachToRecyclerView(binding.recycler)

        return binding.root
    }

    fun cancel() {
        oldJob?.cancel()
        lifecycleScope.launch {
            _adapter.submitData(lifecycle, PagingData.empty())
        }
    }

    fun search(word: String) {
        cancel()
        oldJob = lifecycleScope.launch {
            viewModel.searchNotiInfoList(word).collectLatest {
                _adapter.word = word
                _adapter.submitData(lifecycle, it)
                lifecycleScope.launch {
                    delay(100)
                    val firstVisible = (binding.recycler.layoutManager as LinearLayoutManager)
                        .findFirstVisibleItemPosition()
                    if (firstVisible <= 1) {
                        binding.recycler.scrollToPosition(0)
                    }
                    _adapter.notifyItemChanged(1)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        lifecycleScope.launch {
            delay(100)
            _adapter.notifyDataSetChanged()
        }
    }

    private fun undoDelete(info: NotiInfo) {
        lifecycleScope.launch {
            snack?.dismiss()

            viewModel.deleteInfo = info
            viewModel.undoDelete()

            val message = getString(R.string.snack_deleted2)
            snack = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).run {
                setAction(getString(R.string.snack_undo)) {
                    lifecycleScope.launch {
                        viewModel.undoRestore()
                    }
                }
                addCallback(object : Snackbar.Callback() {
                    val info = viewModel.deleteInfo.copy()
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        when (event) {
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION -> {
                            }
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_CONSECUTIVE,
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_MANUAL,
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE,
                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT -> {
                                viewModel.delete(this.info)
                            }
                        }
                    }
                })
            }
            snack?.show()
        }
    }
}