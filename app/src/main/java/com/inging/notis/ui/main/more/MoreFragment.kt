package com.inging.notis.ui.main.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.inging.notis.R
import com.inging.notis.constant.DarkMode
import com.inging.notis.databinding.MoreFragmentBinding
import com.inging.notis.extension.loadDarkMode

class MoreFragment : Fragment() {

    private lateinit var binding: MoreFragmentBinding

//    private val purchasesUpdatedListener =
//        PurchasesUpdatedListener { billingResult, purchases ->
//            // To be implemented in a later section.
//        }
//
//    private var billingClient = BillingClient.newBuilder(requireContext())
//        .setListener(purchasesUpdatedListener)
//        .enablePendingPurchases()
//        .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.more_fragment, container, false)
        binding.lifecycleOwner = this

        setupDarkMode()
        setupBlockApps()
        setupSaveApps()
        setupRemoveAds()

        return binding.root
    }

    private fun setupRemoveAds() {
//        billingClient.startConnection(object : BillingClientStateListener {
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
//                    // The BillingClient is ready. You can query purchases here.
//                }
//            }
//            override fun onBillingServiceDisconnected() {
//                // Try to restart the connection on the next request to
//                // Google Play by calling the startConnection() method.
//            }
//        })
    }

    private fun setupDarkMode() {
        setupCurrentDarkMode()
        binding.darkMode.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_darkModeFragment)
        }
    }

    private fun setupBlockApps() {
        binding.block.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_blockAppsFragment)
        }
    }

    private fun setupSaveApps() {
        binding.save.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_saveAppsFragment)
        }
    }

    private fun setupCurrentDarkMode() {
        binding.darkModeCurrent.text = when (requireContext().loadDarkMode()) {
            DarkMode.OFF -> getString(R.string.dark_mode_off)
            DarkMode.ON -> getString(R.string.dark_mode_on)
            else -> getString(R.string.dark_mode_system)
        }
    }
}