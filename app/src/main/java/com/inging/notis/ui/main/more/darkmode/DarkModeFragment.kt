package com.inging.notis.ui.main.more.darkmode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.inging.notis.R
import com.inging.notis.constant.DarkMode
import com.inging.notis.databinding.MoreDarkModeFragmentBinding
import com.inging.notis.extension.loadDarkMode
import com.inging.notis.extension.saveDarkMode

class DarkModeFragment : Fragment() {

    private lateinit var binding: MoreDarkModeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.more_dark_mode_fragment, container, false)
        binding.lifecycleOwner = this

        binding.run {
            back.setOnClickListener {
                findNavController().navigateUp()
            }

            radioGroup.check(
                when (requireContext().loadDarkMode()) {
                    DarkMode.OFF -> R.id.off
                    DarkMode.ON -> R.id.on
                    else -> R.id.system
                }
            )

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.off -> {
                        ThemeManager.applyTheme(ThemeManager.ThemeMode.LIGHT)
                        requireContext().saveDarkMode(DarkMode.OFF)
                    }
                    R.id.on -> {
                        ThemeManager.applyTheme(ThemeManager.ThemeMode.DARK)
                        requireContext().saveDarkMode(DarkMode.ON)
                    }
                    R.id.system -> {
                        ThemeManager.applyTheme(ThemeManager.ThemeMode.DEFAULT)
                        requireContext().saveDarkMode(DarkMode.SYSTEM)
                    }
                }
            }
        }

        return binding.root
    }
}