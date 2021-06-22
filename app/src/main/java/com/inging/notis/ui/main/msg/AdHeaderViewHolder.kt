package com.inging.notis.ui.main.msg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.inging.notis.databinding.LayoutAdviewItemBinding
import com.inging.notis.extension.dp2Pixel

/**
 * Created by annasu on 2021/04/26.
 */
class AdHeaderViewHolder(
    private val binding: LayoutAdviewItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Determine the screen width (less decorations) to use for the ad width.
    // If the ad hasn't been laid out, default to the full screen width.
    private val adSize: AdSize
        get() {
//            val display = windowManager.defaultDisplay
//            val outMetrics = DisplayMetrics()
//            display.getMetrics(outMetrics)

            val outMetrics = binding.root.resources.displayMetrics

            val density = outMetrics.density

            var adWidthPixels = binding.layout.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }
            adWidthPixels -=  binding.root.context.dp2Pixel(28f)

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(binding.root.context, adWidth)
        }

    fun bind() {
        binding.run {
//            val adView = AdView(root.context)
//            layout.addView(adView)
//            adView.adUnitId = AdUnitId.MAIN_AD
//            adView.adSize = adSize

            // Create an ad request. Check your logcat output for the hashed device ID to
            // get test ads on a physical device, e.g.,
            // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
            val adRequest = AdRequest
                .Builder()
//            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()

            // Start loading the ad in the background.
            adView.loadAd(adRequest)
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): AdHeaderViewHolder {
            val binding = LayoutAdviewItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return AdHeaderViewHolder(binding)
        }
    }
}