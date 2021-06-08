package com.annasu.notis.ui.main.notification

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.annasu.notis.R
import com.annasu.notis.constant.ClickMode
import com.annasu.notis.constant.NotiListMode
import com.annasu.notis.databinding.MainNotificationFragmentBinding
import com.annasu.notis.ui.main.MainViewModel
import com.annasu.notis.ui.detail.MsgDetailActivity
import com.annasu.notis.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: NotificationViewModel by viewModels()

    private lateinit var binding: MainNotificationFragmentBinding

    private val itemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_notification_fragment, container, false)
        binding.lifecycleOwner = this

        setupAllNotiList()

        // 검색
        binding.toolbar.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.listModeAll.setOnClickListener {
            binding.listModeAll.visibility = View.GONE
            binding.listModePkg.visibility = View.VISIBLE
            viewModel.listMode.set(NotiListMode.PKG)
        }

        binding.listModePkg.setOnClickListener {
            binding.listModeAll.visibility = View.VISIBLE
            binding.listModePkg.visibility = View.GONE
            viewModel.listMode.set(NotiListMode.ALL)
        }

        // 에디트 모드
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

        binding.fabRead.setOnClickListener {
        }

        return binding.root
    }

    private fun setupAllNotiList() {
        val adapter = NotificationAdapter { mode, pkgName, summaryText, isChecked ->
            when (mode) {
                ClickMode.DEFAULT -> {
                    val intent = Intent(context, MsgDetailActivity::class.java)
                    intent.putExtra("PKG_NAME", pkgName)
                    intent.putExtra("SUMMARY_TEXT", summaryText)
                    startActivity(intent)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.allNotiList.collectLatest {
                adapter.submitData(lifecycle, it)
            }
        }

        binding.recycler.adapter = adapter
        binding.recycler.removeItemDecoration(itemDecoration)
        binding.recycler.addItemDecoration(itemDecoration)
        // 애니메이션 제거
//        binding.recycler.itemAnimator = null
    }

    private fun setupPkgNotiList() {
        val adapter = NotificationPkgAdapter { mode, pkgName, summaryText, isChecked ->
            when (mode) {
                ClickMode.DEFAULT -> {
                    val intent = Intent(context, MsgDetailActivity::class.java)
                    intent.putExtra("PKG_NAME", pkgName)
                    intent.putExtra("SUMMARY_TEXT", summaryText)
                    startActivity(intent)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.pkgNotiList.collectLatest {
                adapter.submitData(lifecycle, it)
            }
        }

        binding.recycler.adapter = adapter
        binding.recycler.removeItemDecoration(itemDecoration)
        binding.recycler.addItemDecoration(itemDecoration)
        // 애니메이션 제거
//        binding.recycler.itemAnimator = null
    }
}