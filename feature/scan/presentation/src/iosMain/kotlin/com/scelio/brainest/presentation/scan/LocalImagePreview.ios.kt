@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.scelio.brainest.presentation.scan

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UIViewContentMode

@Composable
actual fun LocalImagePreview(
    imagePath: String,
    modifier: Modifier
) {
    UIKitView(
        modifier = modifier,
        factory = {
            UIImageView().apply {
                clipsToBounds = true
                contentMode = UIViewContentMode.UIViewContentModeScaleAspectFill
            }
        },
        update = { imageView ->
            imageView.image = UIImage.imageWithContentsOfFile(imagePath)
            imageView.contentMode = UIViewContentMode.UIViewContentModeScaleAspectFill
        }
    )
}
