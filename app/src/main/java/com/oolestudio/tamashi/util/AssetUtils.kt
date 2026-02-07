package com.oolestudio.tamashi.util

import androidx.annotation.DrawableRes
import com.oolestudio.tamashi.R

object AssetUtils {
    @DrawableRes
    fun tamashiDrawable(tamashiName: String, assetOverride: String? = null): Int {
        val key = (assetOverride ?: tamashiName).lowercase()
        return when (key) {
            "asset_tamashi_bublu", "bublu" -> R.drawable.asset_tamashi_bublu
            else -> R.drawable.asset_tamashi_bublu
        }
    }
}
