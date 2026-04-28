package com.scelio.brainest.presentation.scan

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.hypot
import kotlin.math.min

@Composable
internal fun CameraPreviewOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.drawWithCache {
            val baseDiameter = min(size.width, size.height) * CameraPreviewCutoutRatio
            val radius = baseDiameter / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val cutoutRect = Rect(
                left = center.x - radius,
                top = center.y - radius,
                right = center.x + radius,
                bottom = center.y + radius
            )

            val overlayPath = Path().apply {
                fillType = PathFillType.EvenOdd
                addRect(Rect(0f, 0f, size.width, size.height))
                addOval(cutoutRect)
            }

            onDrawWithContent {
                drawContent()
                drawPath(path = overlayPath, color = Color.Black.copy(alpha = 0.5f))
            }
        }
    )
}

@Composable
internal fun CameraFullscreenScanOverlay(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    previewBottomInset: Dp
) {
    val progress = animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "fullscreen_scan_progress"
    ).value
    Box(
        modifier = modifier.drawWithCache {
            val previewBottomInsetPx = previewBottomInset.toPx()
            val previewHeight = (size.height - previewBottomInsetPx).coerceAtLeast(0f)
            val finalRadius = min(size.width, previewHeight) * CameraPreviewCutoutRatio / 2f
            val center = Offset(size.width / 2f, previewHeight / 2f)
            val maxDistanceToCorner = maxOf(
                hypot(center.x, center.y),
                hypot(size.width - center.x, center.y),
                hypot(center.x, size.height - center.y),
                hypot(size.width - center.x, size.height - center.y)
            )
            val startRadius = maxDistanceToCorner + 2.dp.toPx()
            val animatedRadius = startRadius + (finalRadius - startRadius) * progress
            val holeRect = Rect(
                left = center.x - animatedRadius,
                top = center.y - animatedRadius,
                right = center.x + animatedRadius,
                bottom = center.y + animatedRadius
            )
            val overlayPath = Path().apply {
                fillType = PathFillType.EvenOdd
                addRect(Rect(0f, 0f, size.width, size.height))
                addOval(holeRect)
            }
            val overlayColor = Color.White.copy(alpha = progress)

            onDrawWithContent {
                drawContent()
                drawPath(path = overlayPath, color = overlayColor)
            }
        }
    )
}
