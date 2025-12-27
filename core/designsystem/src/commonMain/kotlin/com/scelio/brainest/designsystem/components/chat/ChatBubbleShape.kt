package com.scelio.brainest.designsystem.components.chat

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

enum class TrianglePosition {
    LEFT,
    RIGHT
}

class ChatBubbleShape(
    private val trianglePosition: TrianglePosition,
    private val cornerRadius: Dp = 22.dp // Increased slightly to look more "circular"
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radiusPx = with(density) { cornerRadius.toPx() }
        val smallRadiusPx = radiusPx * 0.2f // 20% of the other corners

        val (bottomLeftRadius, bottomRightRadius) = when (trianglePosition) {
            TrianglePosition.LEFT -> {

                smallRadiusPx to radiusPx
            }

            TrianglePosition.RIGHT -> {
                radiusPx to smallRadiusPx
            }
        }

        return Outline.Rounded(
            RoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                topLeftCornerRadius = CornerRadius(radiusPx, radiusPx),
                topRightCornerRadius = CornerRadius(radiusPx, radiusPx),
                bottomRightCornerRadius = CornerRadius(bottomRightRadius, bottomRightRadius),
                bottomLeftCornerRadius = CornerRadius(bottomLeftRadius, bottomLeftRadius)
            )
        )
    }
}