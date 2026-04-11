package com.scelio.brainest.presentation.chat_detail.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatPaginationShimmer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ChatShimmerBubble(widthFraction = 0.76f, lineFractions = listOf(0.88f, 1f, 0.58f))
        ChatShimmerBubble(widthFraction = 0.62f, lineFractions = listOf(0.82f, 0.7f))
        ChatShimmerBubble(widthFraction = 0.84f, lineFractions = listOf(0.9f, 1f, 0.78f, 0.42f))
    }
}

@Composable
fun ChatConversationShimmer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ChatShimmerBubble(
            widthFraction = 0.72f,
            lineFractions = listOf(0.82f, 1f, 0.56f)
        )
        ChatShimmerBubble(
            widthFraction = 0.58f,
            lineFractions = listOf(0.78f, 0.54f),
            alignToEnd = true
        )
        ChatShimmerBubble(
            widthFraction = 0.84f,
            lineFractions = listOf(0.92f, 1f, 0.74f)
        )
        ChatShimmerBubble(
            widthFraction = 0.64f,
            lineFractions = listOf(0.88f, 0.62f),
            alignToEnd = true
        )
    }
}

@Composable
private fun ChatShimmerBubble(
    widthFraction: Float,
    lineFractions: List<Float>,
    alignToEnd: Boolean = false
) {
    val isDarkTheme = isSystemInDarkTheme()
    val transition = rememberInfiniteTransition(label = "chat_pagination_shimmer")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "chat_pagination_progress"
    )

    val bubbleContainerColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.42f)
    }
    val base = if (isDarkTheme) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.78f)
    } else {
        Color(0xFFD7DEE8)
    }
    val highlight = if (isDarkTheme) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
    } else {
        Color(0xFFF7F9FC)
    }
    val brush = Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(progress.value * 700f - 350f, 0f),
        end = Offset(progress.value * 700f, 220f)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (alignToEnd) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .widthIn(max = 420.dp)
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(bubbleContainerColor)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                lineFractions.forEach { fraction ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(14.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(brush)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(52.dp)
                    .height(10.dp)
                    .align(if (alignToEnd) Alignment.End else Alignment.Start)
                    .clip(RoundedCornerShape(999.dp))
                    .background(brush)
            )
        }
    }
}
