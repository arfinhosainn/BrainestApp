package com.scelio.brainest.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
 fun ImagesPreview(
    images: List<ByteArray>,
    imageBase64List: List<String>,
    onImageRemoved: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(images) { index, _ ->
            ImagePreviewItem(
                index = index,
                base64Image = imageBase64List.getOrNull(index) ?: "",
                onRemove = { onImageRemoved(index) }
            )
        }
    }
}