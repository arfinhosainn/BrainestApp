package com.scelio.brainest.presentation.scan

import android.net.Uri
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File

@Composable
actual fun LocalImagePreview(
    imagePath: String,
    modifier: Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                adjustViewBounds = false
            }
        },
        update = { imageView ->
            imageView.setImageURI(Uri.fromFile(File(imagePath)))
        }
    )
}
