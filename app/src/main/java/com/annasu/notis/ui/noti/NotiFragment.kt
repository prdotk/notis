package com.annasu.notis.ui.noti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.annasu.notis.R
import com.annasu.notis.constant.ServiceCommandType.CHECK_REPLY_POSSIBLE
import com.annasu.notis.constant.ServiceCommandType.SEND_MESSAGE_TYPE
import com.annasu.notis.databinding.NotiFragmentBinding
import com.annasu.notis.service.NotisNotificationListenerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotiFragment : Fragment() {

    private val viewModel: NotiViewModel by activityViewModels()

    private lateinit var binding: NotiFragmentBinding

    private lateinit var adapter: NotiAdapter

    private var firstLoad = true

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.noti_fragment, container, false)
        binding.lifecycleOwner = this

        viewModel.recentNotiInfo.observe(viewLifecycleOwner) {
            viewModel.recentNoti = it
        }

        lifecycleScope.launch {
            adapter = NotiAdapter(viewModel.word, viewModel.getLastNotiId())
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

        binding.editText.addTextChangedListener {
            binding.send.isEnabled = !it.isNullOrEmpty()
        }

        binding.send.isEnabled = false
        binding.send.setOnClickListener {
            it.isEnabled = false
            val message = binding.editText.text.toString()
            binding.editText.setText("")
            sendMessage(message)
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            viewModel.readUpdateSummary()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        lifecycleScope.launch {
            delay(50)
            adapter.notifyDataSetChanged()
        }
    }

    // 답장 보낼 수 있는지 확인
    private fun checkReplyPossible() {
        val intent = Intent(context, NotisNotificationListenerService::class.java)
        intent.putExtra("COMMAND_TYPE", CHECK_REPLY_POSSIBLE)
        intent.putExtra("SUMMARY_TEXT", viewModel.summaryText)
        intent.putExtra("RECEIVER", resultReceiver)
        activity?.startService(intent)
    }

    // 답장 보낼 수 있는지 확인한 결과
    private fun checkReplyPossibleResult(resultData: Bundle) {
        // 보내기 정보 사라졌을 경우 입력창 숨기거나 보여줌
        binding.bottomLayout.isInvisible = !resultData.getBoolean("CHECK")
        binding.editText.requestFocus()
    }

    // 답장 보내기
    private fun sendMessage(message: String) {
        val intent = Intent(context, NotisNotificationListenerService::class.java)
        intent.putExtra("COMMAND_TYPE", SEND_MESSAGE_TYPE)
        intent.putExtra("SUMMARY_TEXT", viewModel.summaryText)
        intent.putExtra("MESSAGE_TEXT", message)
        intent.putExtra("RECEIVER", resultReceiver)
        activity?.startService(intent)
    }

    // 답장 보내기 결과
    private fun sendMessageResult(resultData: Bundle) {
        val success = resultData.getBoolean("RESULT")
        val summaryText = resultData.getString("SUMMARY_TEXT") ?: ""
        val messageText = resultData.getString("MESSAGE_TEXT") ?: ""

        if (success) {
            viewModel.saveMessage(summaryText, messageText)
        }
    }
}