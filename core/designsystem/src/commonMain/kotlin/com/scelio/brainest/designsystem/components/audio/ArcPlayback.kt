package com.scelio.brainest.designsystem.components.audio

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun DividedCircleCanvas(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(3.3333f)
) {
    BoxWithConstraints(modifier = modifier) {
        val radiusY = (maxWidth / 2f) * 0.72f
        val rowButtonSize = 60.dp
        val middleButtonSize = 90.dp

        Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { scaleX = 1.05f }) {
            val radiusX = size.width / 2f
            val radiusY = radiusX * 0.72f
            val center = Offset(size.width / 2f, size.height)

            // Top half (upper semi-ellipse), flattened a bit more in height
            val topHalfPath = createUpperSemicirclePath(center, radiusX, radiusY)
            drawPath(
                path = topHalfPath,
                color = Color.White   // change color/alpha as needed
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth().padding(start = 25.dp, end = 25.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EchoPlaybackButton(
                playbackState = PlaybackState.PLAYING,
                onPauseClick = {},
                onPlayClick = {},
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF19C472),
                    contentColor = Color.White
                ),
                modifier = Modifier.size(rowButtonSize)
            )
            Box(
                modifier = Modifier
                    .height(rowButtonSize)
                    .width(140.dp),
                contentAlignment = Alignment.Center
            ) {
                StopwatchTimer()
            }
            EchoPlaybackButton(
                playbackState = PlaybackState.PAUSED,
                onPauseClick = {},
                onPlayClick = {},
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF19C472),
                    contentColor = Color.White
                ),
                modifier = Modifier.size(rowButtonSize)
            )
        }

        EchoPlaybackButton(
            playbackState = PlaybackState.PAUSED,
            onPauseClick = {},
            onPlayClick = {},
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color(0xFF19C472),
                contentColor = Color.White
            ),
            modifier = Modifier
                .size(middleButtonSize)
                .offset(
                    x = (maxWidth - middleButtonSize) / 2f,
                    y = maxHeight - radiusY - (middleButtonSize / 2f)
                )
        )
    }
}


private fun DrawScope.createUpperSemicirclePath(
    center: Offset,
    radiusX: Float,
    radiusY: Float
): Path = Path().apply {
    val rect = Rect(
        left = center.x - radiusX,
        top = center.y - radiusY,
        right = center.x + radiusX,
        bottom = center.y + radiusY
    )

    // Start at left point of the diameter
    moveTo(center.x - radiusX, center.y)

    // Arc from 180° (left) → 360°/0° (right) going COUNTER-CLOCKWISE through the TOP
    // This is mathematically exact for the upper half (uses Compose's standard angle system)
    arcTo(
        rect = rect,
        startAngleDegrees = 180f,
        sweepAngleDegrees = 180f,
        forceMoveTo = false
    )

    // Close the shape by going back along the diameter
    lineTo(center.x - radiusX, center.y)
    close()
}


@Preview
@Composable
fun PreviewSheet(){
    BrainestTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            DividedCircleCanvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3.3333f)
                    .align(Alignment.BottomCenter)
            )
        }

    }
}
