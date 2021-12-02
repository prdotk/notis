package com.inging.notis.ui.detail.msg

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.inging.notis.R
import com.inging.notis.constant.ClickMode
import com.inging.notis.constant.ServiceCommandType.CHECK_REPLY_POSSIBLE
import com.inging.notis.constant.ServiceCommandType.SEND_MESSAGE_TYPE
import com.inging.notis.databinding.MsgDetailFragmentBinding
import com.inging.notis.extension.showBottomSheetDialog
import com.inging.notis.service.NotisNotificationListenerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MsgDetailFragment : Fragment() {

    private val viewModel: MsgDetailViewModel by activityViewModels()

    private lateinit var binding: MsgDetailFragmentBinding

    private lateinit var adapter: MsgDetailAdapter

    private var firstLoad = true

    private var isPossibleSend = false

    private var snack: Snackbar? = null

    private val resultReceiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            super.onReceiveResult(resultCode, resultData)
            resultData?.let {
                when (resultCode) {
                    0 -> sendMessageResult(it)
                    1 -> checkReplyPossibleResult(it)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.msg_detail_fragment, container, false)
        binding.lifecycleOwner = this

        viewModel.recentNotiInfo.observe(viewLifecycleOwner) {
            viewModel.recentNoti = it
        }

        lifecycleScope.launch {
            adapter = MsgDetailAdapter(
                viewModel.word,
                viewModel.isEditMode,
                viewModel.selectedList
            ) { mode, info, isChecked, position ->
                when (mode) {
                    ClickMode.CHECK -> // 체크 버튼 클릭 시 액션
                        if (isChecked) viewModel.selectedList.add(info.notiId)
                        else viewModel.selectedList.remove(info.notiId)
                    ClickMode.LONG -> //viewModel.isEditMode.set(true)
                        requireContext().showBottomSheetDialog(info) {
                            viewModel.selectedList.add(info.notiId)
                            undoDelete(position)
//                        viewModel.isEditMode.set(true)
//                        viewModel.selectedList.add(
//                            SimpleSummaryData(info.pkgName, info.summaryText)
//                        )
                        }
//                        requireContext().showBottomSheetDialog(info) {
//                            lifecycleScope.launch {
//                                viewModel.realDelete(info.notiId)
//                                Toast.makeText(
//                                    requireContext(),
//                                    R.string.snack_deleted,
//                                    Toast.LENGTH_LONG
//                                ).show()
//                                delay(300)
//                                adapter.notifyItemChanged(position - 1)
//                            }
//                        }
                }
            }

            binding.recycler.itemAnimator = null
            binding.recycler.adapter = adapter

            viewModel.notiInfoList.collectLatest {
                adapter.submitData(lifecycle, it)
                checkReplyPossible()
                lifecycleScope.launch {
                    delay(1000)
                    if (firstLoad) {
                        firstLoad = false
                        binding.recycler.scrollToPosition(viewModel.findPosition())
                    } else {
//                        adapter.notifyItemChanged(1)
                        adapter.notifyItemRangeChanged(0, 2)
                        val firstVisible = (binding.recycler.layoutManager as LinearLayoutManager)
                            .findFirstVisibleItemPosition()
                        if (firstVisible <= 1) {
                            binding.recycler.smoothScrollToPosition(0)
                        }
                    }
                }
            }
        }

        viewModel.readUpdateSummary()

        // 애니메이션 제거
//        (binding.recycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
//        (binding.recycler.itemAnimator as SimpleItemAnimator).changeDuration = 0
//        binding.recycler.itemAnimator = NoAnimationItemAnimator()

        binding.input.addTextChangedListener {
            binding.send.isEnabled = !it.isNullOrEmpty()
        }

        binding.send.isEnabled = false
        binding.send.setOnClickListener {
            it.isEnabled = false
            val message = binding.input.text.toString()
            binding.input.setText("")
            sendMessage(message)
        }

        viewModel.isEditMode.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender is ObservableBoolean) {
                    val isEditMode = sender.get()
                    binding.bottomLayout.isVisible = !isEditMode && isPossibleSend
                }
            }
        })

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        viewModel.readUpdateSummary()
    }

    // 답장 보낼 수 있는지 확인
    private fun checkReplyPossible() {
        val intent = Intent(context, NotisNotificationListenerService::class.java)
        intent.putExtra("COMMAND_TYPE", CHECK_REPLY_POSSIBLE)
        intent.putExtra("PKG_NAME", viewModel.pkgName)
        intent.putExtra("SUMMARY_TEXT", viewModel.summaryText)
        intent.putExtra("RECEIVER", resultReceiver)
        activity?.startService(intent)
    }

    // 답장 보낼 수 있는지 확인한 결과
    private fun checkReplyPossibleResult(resultData: Bundle) {
        // 보내기 정보 사라졌을 경우 입력창 숨기거나 보여줌
        isPossibleSend = resultData.getBoolean("CHECK")

        binding.bottomLayout.isVisible = isPossibleSend

//        binding.send.isVisible = checkPossible
//        binding.editText.isEnabled = checkPossible

        binding.input.requestFocus()
    }

    // 답장 보내기
    private fun sendMessage(message: String) {
        val intent = Intent(context, NotisNotificationListenerService::class.java)
        intent.putExtra("COMMAND_TYPE", SEND_MESSAGE_TYPE)
        intent.putExtra("PKG_NAME", viewModel.pkgName)
        intent.putExtra("SUMMARY_TEXT", viewModel.summaryText)
        intent.putExtra("MESSAGE_TEXT", message)
        intent.putExtra("RECEIVER", resultReceiver)
        activity?.startService(intent)
    }

    // 답장 보내기 결과
    private fun sendMessageResult(resultData: Bundle) {
        val success = resultData.getBoolean("RESULT")
        val messageText = resultData.getString("MESSAGE_TEXT") ?: ""

        binding.bottomLayout.isVisible = success

        if (success) {
            viewModel.saveMessage(messageText)
        }
    }

    fun undoDelete(position: Int?) {
        lifecycleScope.launch {
            snack?.dismiss()

            viewModel.deleteList = viewModel.selectedList.toList()
            viewModel.undoDelete()

            finishEditMode()

            delay(150)
            position?.let {
                adapter.notifyItemRangeChanged(position - 1, 2)
            } ?: run {
                adapter.notifyDataSetChanged()
            }

            val message = "${viewModel.deleteList.size} ${getString(R.string.snack_deleted)}"
            snack = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).run {
                setAction(getString(R.string.snack_undo)) {
                    lifecycleScope.launch {
                        viewModel.undoRestore()
                        viewModel.selectedList.clear()

                        delay(150)
                        position?.let {
                            adapter.notifyItemRangeChanged(position - 1, 3)
                        } ?: run {
                            adapter.notifyDataSetChanged()
                        }
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
}