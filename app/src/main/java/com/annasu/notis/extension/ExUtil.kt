package com.annasu.notis.extension

import android.content.Context

/**
 * Created by annasu on 2021/05/14.
 */
fun Context.dp2Pixel(dp: Float): Int {
    val metrics = resources.displayMetrics
    return (dp * (metrics.densityDpi / 160f)).toInt()
}