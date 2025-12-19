package com.scelio.brainest.presentation.chat_detail.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import brainest.feature.chat.presentation.generated.resources.Res
import brainest.feature.chat.presentation.generated.resources.ic_mic
import org.jetbrains.compose.resources.painterResource

@Composable
 fun TrailingButton(
    hasContent: Boolean,
    enabled: Boolean,
    onSendClick: () -> Unit,
    onMicClick: () -> Unit
) {
    if (hasContent) {
        IconButton(
            onClick = onSendClick,
            enabled = enabled,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Send message"
            )
        }
    } else {
        IconButton(
            onClick = onMicClick,
            enabled = enabled,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_mic),
                contentDescription = "Record voice",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}