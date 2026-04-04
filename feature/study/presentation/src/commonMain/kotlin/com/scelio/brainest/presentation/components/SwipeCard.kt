package com.scelio.brainest.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeCard(
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    swipeThreshold: Float = 300f,
    sensitivityFactor: Float = 3f,
    content: @Composable () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val density = LocalDensity.current.density
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .graphicsLayer {
                rotationZ = (offsetX.value / 30f).coerceIn(-15f, 15f)
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            offsetX.value > swipeThreshold -> {
                                scope.launch {
                                    offsetX.animateTo(2000f, spring(stiffness = 200f))
                                    onSwipeRight()
                                    offsetX.snapTo(0f)
                                }
                            }

                            offsetX.value < -swipeThreshold -> {
                                scope.launch {
                                    offsetX.animateTo(-2000f, spring(stiffness = 200f))
                                    onSwipeLeft()
                                    offsetX.snapTo(0f)
                                }
                            }

                            else -> {
                                scope.launch {
                                    offsetX.animateTo(
                                        0f,
                                        spring(dampingRatio = 0.6f, stiffness = 300f)
                                    )
                                }
                            }
                        }
                    }
                ) { change, dragAmount ->
                    if (change.positionChange() != Offset.Zero) change.consume()
                    scope.launch {
                        offsetX.snapTo(offsetX.value + (dragAmount / density) * sensitivityFactor)
                    }
                }
            }
    ) { content() }
}
