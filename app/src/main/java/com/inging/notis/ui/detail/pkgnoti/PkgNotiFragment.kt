package com.inging.notis.ui.detail.pkgnoti

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.inging.notis.R
import com.inging.notis.constant.ClickMode
import com.inging.notis.databinding.PkgNotiFragmentBinding
import com.inging.notis.ui.detail.msg.MsgDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PkgNotiFragment : Fragment() {

    private val viewModel: PkgNotiViewModel by activityViewModels()

    private lateinit var binding: PkgNotiFragmentBinding

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

        setupNotiList()

        // 에디트 모드
        viewModel.isMsgEditMode.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender is ObservableBoolean) {
                    val isEditMode = sender.get()
//                    binding.search.isVisible = !isEditMode
//                    binding.cancel.isVisible = isEditMode
                    binding.fabRead.isVisible = !isEditMode
                    binding.fabDelete.isVisible = isEditMode
                }
            }
        })

        binding.fabRead.setOnClickListener {
        }

        return binding.root
    }

    private fun setupNotiList() {
        val adapter = PkgNotiAdapter(
            viewModel.isEditMode,
            viewModel.deleteList
        ) { mode, info, isChecked ->
            when (mode) {
                ClickMode.DEFAULT -> {
                    val intent = Intent(context, MsgDetailActivity::class.java)
                    intent.putExtra("PKG_NAME", info.pkgName)
                    intent.putExtra("SUMMARY_TEXT", info.summaryText)
                    startActivity(intent)
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
            }
        }

        binding.recycler.run {
//            itemAnimator = null
            this.adapter = adapter
            removeItemDecoration(itemDecoration)
            addItemDecoration(itemDecoration)
        }
    }
}