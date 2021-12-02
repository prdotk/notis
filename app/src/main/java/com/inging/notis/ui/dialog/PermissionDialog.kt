package com.inging.notis.ui.dialog

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.inging.notis.databinding.LayoutPermissionDialogBinding
import com.inging.notis.extension.getAppIcon
import com.inging.notis.extension.permissionNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PermissionDialog : DialogFragment() {

    private lateinit var binding: LayoutPermissionDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutPermissionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch(Dispatchers.Main) {
            binding.icon.setImageDrawable(requireContext().getAppIcon())
        }

        binding.allow.setOnClickListener {
            context?.permissionNotification()
        }

        binding.dummy.setOnClickListener(null)

        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                activity?.finish()
                return@setOnKeyListener true
            }
            false
        }
    }
}