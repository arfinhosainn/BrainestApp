package com.brainest.presentation.introduction.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@Composable
fun PagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF2D2D3A),
    inactiveColor: Color = Color.LightGray,
    dotSize: Dp = 12.dp,
    dotSpacing: Dp = 5.dp
) {
    val density = LocalDensity.current

    Row(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Calculate total width for positioning
        val totalDotsWidth = with(density) {
            (pagerState.pageCount * dotSize.toPx()) + ((pagerState.pageCount - 1) * dotSpacing.toPx())
        }

        // Container for the animated indicator
        Box(
            modifier = Modifier
                .width(with(density) { totalDotsWidth.toDp() })
                .height(dotSize),
            contentAlignment = Alignment.CenterStart
        ) {
            // Draw inactive dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(dotSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pagerState.pageCount) { _ ->
                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .clip(CircleShape)
                            .background(inactiveColor)
                    )
                }
            }

            // Animated active indicator - "water drop" effect
            val targetPage = pagerState.targetPage
            val currentPage = pagerState.currentPage
            val currentPageOffset = pagerState.currentPageOffsetFraction

            // Calculate the position with offset
            val animatedPosition by animateFloatAsState(
                targetValue = currentPage + currentPageOffset,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "indicator_position"
            )

            // Calculate stretch effect based on movement
            val offsetDelta = (targetPage - currentPage + currentPageOffset).coerceIn(-1f, 1f)
            val stretchFactor = 1f + (offsetDelta.absoluteValue * 0.4f)

            val animatedScale by animateFloatAsState(
                targetValue = stretchFactor,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "indicator_scale"
            )

            // Calculate the x position
            val spacingPx = with(density) { (dotSize + dotSpacing).toPx() }
            val indicatorX = animatedPosition * spacingPx

            // Water drop effect - stretches horizontally while moving
            Box(
                modifier = Modifier
                    .offset(x = with(density) { indicatorX.toDp() })
                    .size(dotSize)
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = 1f / (animatedScale.coerceAtLeast(0.7f)) // Squash vertically when stretching horizontally
                    }
                    .clip(CircleShape)
                    .background(activeColor)
            )
        }
    }
}

// Alternative: Worm-style indicator that stretches between dots
@Composable
fun WormPagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF2D2D3A),
    inactiveColor: Color = Color.LightGray,
    dotSize: Dp = 8.dp,
    dotSpacing: Dp = 8.dp
) {
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dotSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pagerState.pageCount) { _ ->
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(inactiveColor)
                )
            }
        }

        // Worm indicator that stretches between pages
        val currentPage = pagerState.currentPage
        val targetPage = pagerState.targetPage
        val offset = pagerState.currentPageOffsetFraction

        val animatedProgress by animateFloatAsState(
            targetValue = currentPage + offset,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "worm_progress"
        )

        val spacingPx = with(density) { (dotSize + dotSpacing).toPx() }
        val dotSizePx = with(density) { dotSize.toPx() }

        // Calculate worm width based on page offset
        val pageDelta = (targetPage - currentPage + offset).coerceIn(-1f, 1f)
        val stretchWidth = dotSizePx + (spacingPx * pageDelta.absoluteValue)

        val animatedWidth by animateDpAsState(
            targetValue = with(density) { stretchWidth.toDp() },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "worm_width"
        )

        val startPosition = animatedProgress * spacingPx

        Box(
            modifier = Modifier
                .offset(x = with(density) { startPosition.toDp() })
                .size(height = dotSize, width = animatedWidth)
                .clip(CircleShape)
                .background(activeColor)
        )
    }
}
