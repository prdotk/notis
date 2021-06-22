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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.inging.notis.R
import com.inging.notis.constant.ClickMode
import com.inging.notis.constant.ServiceCommandType
import com.inging.notis.databinding.PkgNotiFragmentBinding
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

    private val itemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.pkg_noti_fragment, container, false)
        binding.lifecycleOwner = this

        val adapter = PkgNotiAdapter(
            viewModel.word,
            viewModel.isEditMode,
            viewModel.deleteList
        ) { mode, info, isChecked ->
            when (mode) {
                ClickMode.DEFAULT -> {
                    val intent = Intent(context, NotisNotificationListenerService::class.java)
                    intent.putExtra("COMMAND_TYPE", ServiceCommandType.RUN_CONTENT_INTENT)
                    intent.putExtra("PKG_NAME", info.pkgName)
                    intent.putExtra("NOTI_ID", info.notiId)
                    activity?.startService(intent)
//                    val intent = Intent(context, MsgDetailActivity::class.java)
//                    intent.putExtra("PKG_NAME", info.pkgName)
//                    intent.putExtra("SUMMARY_TEXT", info.summaryText)
//                    startActivity(intent)
                }
                ClickMode.CHECK -> // 체크 버튼 클릭 시 액션
                    if (isChecked) viewModel.deleteList.add(info)
                    else viewModel.deleteList.remove(info)
                ClickMode.LONG -> viewModel.isEditMode.set(true)
            }
        }

        lifecycleScope.launch {
            viewModel.pkgNotiList(viewModel.pkgName).collectLatest {
                adapter.submitData(lifecycle, it)

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

        binding.recycler.run {
//            itemAnimator = null
            this.adapter = adapter
            removeItemDecoration(itemDecoration)
            addItemDecoration(itemDecoration)
        }

        return binding.root
    }
}