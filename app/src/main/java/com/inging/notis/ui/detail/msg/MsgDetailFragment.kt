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
import com.inging.notis.R
import com.inging.notis.constant.ClickMode
import com.inging.notis.constant.ServiceCommandType.CHECK_REPLY_POSSIBLE
import com.inging.notis.constant.ServiceCommandType.SEND_MESSAGE_TYPE
import com.inging.notis.databinding.MsgDetailFragmentBinding
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
                viewModel.word, viewModel.getLastNotiId(),
                viewModel.isEditMode,
                viewModel.deleteList
            ) { mode, notiId, isChecked ->
                when (mode) {
                    ClickMode.CHECK ->
                        // 체크 버튼 클릭 시 액션
                        if (isChecked)
                            viewModel.deleteList.add(notiId)
                        else
                            viewModel.deleteList.remove(notiId)
                    ClickMode.LONG -> viewModel.isEditMode.set(true)
                }
            }
            binding.recycler.adapter = adapter

            viewModel.notiInfoList.collectLatest {
                adapter.submitData(lifecycle, it)
                checkReplyPossible()
                lifecycleScope.launch {
                    delay(50)
                    if (firstLoad) {
                        firstLoad = false
                        binding.recycler.scrollToPosition(viewModel.findPosition())
                    } else {
                        val firstVisible = (binding.recycler.layoutManager as LinearLayoutManager)
                            .findFirstVisibleItemPosition()
                        if (firstVisible <= 1) {
                            binding.recycler.scrollToPosition(0)
                        }
                        adapter.notifyItemChanged(1)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.readUpdateSummary()
        }

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
        lifecycleScope.launch {
            viewModel.readUpdateSummary()
        }
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

        if (success) {
            viewModel.saveMessage(messageText)
        }
    }
}