package com.scelio.brainest.presentation.chat_detail.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import brainest.feature.chat.presentation.generated.resources.ic_arrow_up
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
            modifier = Modifier.size(30.dp),
            onClick = onSendClick,
            enabled = enabled,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Icon(
                modifier = Modifier.size(15.dp),

                painter = painterResource(Res.drawable.ic_arrow_up),
                contentDescription = "Send message"
            )
        }
    } else {
        IconButton(
            modifier = Modifier.size(30.dp),
            onClick = onMicClick,
            enabled = enabled,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(

                painter = painterResource(Res.drawable.ic_mic),
                contentDescription = "Record voice",
                modifier = Modifier.size(15.dp)
            )
        }
    }
}