package com.oolestudio.tamashi.ui.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.oolestudio.tamashi.util.AssetUtils
import com.oolestudio.tamashi.util.tutorial.TutorialLayoutUtils

@Composable
fun TamashiAvatar(
    tamashiName: String,
    assetOverride: String? = null,
    modifier: Modifier = Modifier
) {
    val resId = AssetUtils.tamashiDrawable(tamashiName, assetOverride)
    Image(
        painter = painterResource(resId),
        contentDescription = "Tamashi bublu",
        modifier = modifier.size(TutorialLayoutUtils.avatarSize)
    )
}
