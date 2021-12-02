package com.inging.notis.ui.main.more.save

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.inging.notis.R
import com.inging.notis.constant.ClickMode
import com.inging.notis.databinding.MoreAppsFragmentBinding
import com.inging.notis.extension.hideKeyboard
import com.inging.notis.extension.showKeyboard
import com.inging.notis.ui.main.more.MoreViewModel
import kotlinx.coroutines.launch

class SaveAppsFragment : Fragment() {

    private val viewModel: MoreViewModel by activityViewModels()

    private lateinit var binding: MoreAppsFragmentBinding

    private var callback: OnBackPressedCallback? = null

    // 전체 on off
    // 0:off, 1:on, 2:none
    private val allOnOff = ObservableInt(2)

    private val _adapter: SaveAppsAdapter by lazy {
        SaveAppsAdapter(allOnOff) { mode, info ->
            when (mode) {
                ClickMode.DEFAULT -> {
                    viewModel.savePkgInfo(info)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.more_apps_fragment, container, false)
        binding.lifecycleOwner = this

        binding.run {
            title.text = getText(R.string.more_save_notifications)

            back.setOnClickListener {
                backAction()
                context?.hideKeyboard(input)
            }

            search.setOnClickListener {
                showSearchBar()
            }

            cancel.setOnClickListener {
                binding.input.text.clear()
            }

            recycler.run {
//            itemAnimator = null
                adapter = _adapter.apply {
                    lifecycleScope.launch {
                        spinKit.visibility = View.VISIBLE
                        viewModel.getSaveAppList(requireContext())
                        adapterSubmitList()
                        spinKit.visibility = View.GONE
                        search.visibility = View.VISIBLE
                        menu.visibility = View.VISIBLE
                    }
                }
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }

            input.addTextChangedListener {
                val word = it.toString().lowercase()

                cancel.isVisible = word.isNotEmpty()

                _adapter.submitList(
                    when {
                        word.isNotEmpty() -> viewModel.appInfoList.filter { info ->
                            info.label.lowercase().contains(word)
                        }
                        viewModel.showSystemApp -> viewModel.appInfoList
                        else -> viewModel.appInfoList.filter { info ->
                            !info.isSystemApp
                        }
                    })
            }

            // 상단 메뉴 컨텍스트
            menu.setOnClickListener { v ->
                PopupMenu(requireContext(), v).run {
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            // 시스템 앱 보이기/숨기기
                            R.id.system_apps -> {
                                viewModel.showSystemApp = !viewModel.showSystemApp
                                adapterSubmitList()
                                true
                            }
                            R.id.all_on -> {
                                allOn()
                                true
                            }
                            R.id.all_off -> {
                                allOff()
                                true
                            }
                            else -> false
                        }
                    }
                    menuInflater.inflate(R.menu.menu_setting_app, menu)
                    menu.getItem(0).title =
                        if (viewModel.showSystemApp) getString(R.string.setting_menu_hide_system_apps)
                        else getString(R.string.setting_menu_show_system_apps)

                    show()
                }
            }
        }
        return binding.root
    }

    private fun adapterSubmitList() {
        _adapter.submitList(
            when {
                viewModel.showSystemApp -> viewModel.appInfoList
                else -> viewModel.appInfoList.filter { info ->
                    !info.isSystemApp
                }
            })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backAction()
            }
        }.apply {
            requireActivity().onBackPressedDispatcher.addCallback(this@SaveAppsFragment, this)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback?.remove()
    }

    private fun backAction() {
        if (binding.input.visibility == View.VISIBLE) {
            hideSearchBar()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showSearchBar() {
        binding.run {
            search.visibility = View.GONE
            menu.visibility = View.GONE
            title.visibility = View.GONE

            input.run {
                visibility = View.VISIBLE
                requestFocus()
                context.showKeyboard(this)
            }
        }
    }

    private fun hideSearchBar() {
        binding.run {
            search.visibility = View.VISIBLE
            menu.visibility = View.VISIBLE
            title.visibility = View.VISIBLE

            input.run {
                visibility = View.GONE
                text.clear()
            }
        }
    }

    private fun allOn() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.setting_save_all_on)
            .setPositiveButton(R.string.dialog_ok) { _, _ ->
                viewModel.appInfoList.forEach { info -> info.isSave = true }
                allOnOff.set(1)
                viewModel.allSaveSet(true)
            }.setNegativeButton(R.string.dialog_cancel) { _, _ ->
            }.create().show()
    }

    private fun allOff() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.setting_save_all_off)
            .setPositiveButton(R.string.dialog_ok) { _, _ ->
                viewModel.appInfoList.forEach { info -> info.isSave = false }
                allOnOff.set(0)
                viewModel.allSaveSet(false)
            }.setNegativeButton(R.string.dialog_cancel) { _, _ ->
            }.create().show()
    }
}