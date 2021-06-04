package com.annasu.notis.ui.main.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.annasu.notis.R
import com.annasu.notis.constant.ClickMode
import com.annasu.notis.data.model.SimpleSummaryData
import com.annasu.notis.databinding.MainMessageFragmentBinding
import com.annasu.notis.extension.dp2Pixel
import com.annasu.notis.ui.custom.LinearLayoutItemDecoration
import com.annasu.notis.ui.main.MainViewModel
import com.annasu.notis.ui.noti.NotiActivity
import com.annasu.notis.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: NotificationViewModel by viewModels()

    private lateinit var binding: MainMessageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_message_fragment, container, false)
        binding.lifecycleOwner = this

        val adapter = NotificationAdapter(
            mainViewModel.isMsgEditMode,
            viewModel.removeList
        ) { mode, pkgName, summaryText, isChecked ->
            when (mode) {
                ClickMode.DEFAULT -> {
                    val intent = Intent(context, NotiActivity::class.java)
                    intent.putExtra("PKG_NAME", pkgName)
                    intent.putExtra("SUMMARY_TEXT", summaryText)
                    startActivity(intent)
                }
                ClickMode.CHECK ->
                    // 체크 버튼 클릭 시 액션
                    if (isChecked)
                        viewModel.removeList.add(SimpleSummaryData(pkgName, summaryText))
                    else
                        viewModel.removeList.remove(SimpleSummaryData(pkgName, summaryText))
                ClickMode.LONG -> mainViewModel.isMsgEditMode.set(true)
            }
        }

        lifecycleScope.launch {
            viewModel.messageList.collectLatest {
                adapter.submitData(lifecycle, it)
            }
        }

        // 검색
        binding.title.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)
        }

        // 취소
        binding.cancel.setOnClickListener {
            lifecycleScope.launch {
                finishEditMode()
            }
        }

        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            LinearLayoutItemDecoration(
                requireContext().dp2Pixel(5f), requireContext().dp2Pixel(5f)
            )
        )
        // 애니메이션 제거
//        binding.recycler.itemAnimator = null

        mainViewModel.isMsgEditMode.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender is ObservableBoolean) {
                    val isEditMode = sender.get()
//                    binding.search.isVisible = !isEditMode
                    binding.cancel.isVisible = isEditMode
                    binding.fabRead.isVisible = !isEditMode
                    binding.fabDelete.isVisible = isEditMode
                }
            }
        })

        binding.fabRead.setOnClickListener {
        }

        binding.fabDelete.setOnClickListener {
            lifecycleScope.launch {
                removeMessage()
            }
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.onBackPressedDispatcher?.addCallback {
            if (mainViewModel.isMsgEditMode.get()) {
                finishEditMode()
            } else {
                activity?.finish()
            }
        }
    }

    fun finishEditMode() {
        viewModel.clearRemoveList()
        mainViewModel.isMsgEditMode.set(false)
    }

    private fun removeMessage() {
        lifecycleScope.launch {
            viewModel.remove()
            finishEditMode()
        }
    }
}