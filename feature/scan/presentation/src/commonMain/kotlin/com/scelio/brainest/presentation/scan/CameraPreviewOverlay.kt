package com.scelio.brainest.presentation.scan

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
internal fun CameraSquareOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.drawWithCache {
            val cutoutSize = min(size.width, size.height) * 0.85f
            val cutoutLeft = (size.width - cutoutSize) / 2f
            val cutoutTop = (size.height - cutoutSize) / 2f
            val cornerRadius = 18.dp.toPx()
            val cutoutRect = Rect(
                left = cutoutLeft,
                top = cutoutTop,
                right = cutoutLeft + cutoutSize,
                bottom = cutoutTop + cutoutSize
            )

            val overlayPath = Path().apply {
                fillType = PathFillType.EvenOdd
                addRect(Rect(0f, 0f, size.width, size.height))
                addRoundRect(
                    RoundRect(
                        rect = cutoutRect,
                        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                    )
                )
            }

            onDrawWithContent {
                drawContent()
                drawPath(
                    path = overlayPath,
                    color = Color.Black.copy(alpha = 0.3f)
                )
            }
        }
    )
}
