package com.scelio.brainest.presentation.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.unit.dp
import brainest.feature.scan.presentation.generated.resources.Res
import brainest.feature.scan.presentation.generated.resources.ic_gallery
import brainest.feature.scan.presentation.generated.resources.ic_keyboard
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun CameraBottomControls(
    modifier: Modifier = Modifier,
    isCaptureEnabled: Boolean = true,
    onGalleryClick: () -> Unit,
    onCaptureClick: () -> Unit,
    onTypeClick: () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(CameraControlsPanelHeight)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF262831),
                        Color(0xFF1E2027)
                    )
                )
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        val extraBottomPadding = maxHeight * CameraControlsButtonLiftRatio

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 16.dp,
                    bottom = 16.dp + extraBottomPadding
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CameraBottomAction(
                    label = "Gallery",
                    onClick = onGalleryClick
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_gallery),
                        contentDescription = "Gallery",
                        modifier = modifier.size(30.dp),
                        tint = Color.White
                    )
                }
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CameraCaptureButton(
                    enabled = isCaptureEnabled,
                    onClick = onCaptureClick
                )
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CameraBottomAction(
                    label = "Type",
                    onClick = onTypeClick
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_keyboard),
                        contentDescription = "Type",
                        modifier = modifier.size(30.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraBottomAction(
    label: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        icon()
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
    }
}
