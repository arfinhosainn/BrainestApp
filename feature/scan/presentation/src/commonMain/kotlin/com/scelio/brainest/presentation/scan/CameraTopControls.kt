package com.scelio.brainest.presentation.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import brainest.feature.scan.presentation.generated.resources.Res
import brainest.feature.scan.presentation.generated.resources.ic_closed
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun CameraTopControls(
    modifier: Modifier = Modifier,
    isFlashOn: Boolean,
    isFlashAvailable: Boolean,
    onCloseClick: () -> Unit,
    onFlashClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CameraTopIconButton(
            onClick = onCloseClick
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_closed),
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Box(modifier = Modifier.weight(1f))
        CameraTopIconButton(
            enabled = isFlashAvailable,
            onClick = onFlashClick
        ) {
            Icon(
                imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = "Flash",
                tint = if (isFlashAvailable) Color.White else Color(0x80FFFFFF)
            )
        }
    }
}

@Composable
private fun CameraTopIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .background(
                color = if (enabled) Color.White.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.22f),
                shape = CircleShape
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun PreiewCameraTopControl() {
    CameraTopControls(
        isFlashOn = true,
        isFlashAvailable = true,
        onCloseClick = {},
        onFlashClick = {}
    )
}
