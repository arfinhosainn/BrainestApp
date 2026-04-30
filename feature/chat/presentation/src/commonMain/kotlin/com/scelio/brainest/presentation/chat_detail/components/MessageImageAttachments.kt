package com.scelio.brainest.presentation.chat_detail.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun MessageImageAttachments(
    imageUrls: List<String>,
    modifier: Modifier = Modifier
) {
    if (imageUrls.isEmpty()) return

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        imageUrls.forEachIndexed { index, imageUrl ->
            AsyncImage(
                model = normalizeImageModel(imageUrl),
                contentDescription = "Message attachment ${index + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

private fun normalizeImageModel(imageUrl: String): String {
    val normalized = imageUrl.trim()
    if (normalized.startsWith("data:", ignoreCase = true)) return normalized
    if (normalized.startsWith("http://", ignoreCase = true)) return normalized
    if (normalized.startsWith("https://", ignoreCase = true)) return normalized
    if (normalized.startsWith("file://", ignoreCase = true)) return normalized
    if (normalized.startsWith("/")) return normalized
    return "data:image/jpeg;base64,$normalized"
}
