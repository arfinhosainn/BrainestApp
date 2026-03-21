package com.scelio.brainest.presentation.components


import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class CardFace { Front, Back }

@Composable
fun FlipCard(
    face: CardFace,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    front: @Composable BoxScope.() -> Unit,
    back: @Composable BoxScope.() -> Unit,
) {
    val transition = updateTransition(face, label = "flip")

    val rotation by transition.animateFloat(
        transitionSpec = { tween(800) },
        label = "rotation"
    ) { state ->
        when (state) {
            CardFace.Front -> 0f
            CardFace.Back -> 180f
        }
    }

    val frontAlpha by transition.animateFloat(
        transitionSpec = { tween(400) },
        label = "frontAlpha"
    ) { state ->
        when (state) {
            CardFace.Front -> 1f
            CardFace.Back -> 0f
        }
    }

    val backAlpha by transition.animateFloat(
        transitionSpec = { tween(400) },
        label = "backAlpha"
    ) { state ->
        when (state) {
            CardFace.Front -> 0f
            CardFace.Back -> 1f
        }
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 16 * density
            }
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Front face
            if (rotation <= 90f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = frontAlpha },
                    content = front
                )
            } else {
                // Back face - needs to be flipped
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = 180f
                            alpha = backAlpha
                        },
                    content = back
                )
            }
        }
    }
}

private class TransitionData(
    color: State<Color>,
    rotation: State<Float>,
    animateFront: State<Float>,
    animateBack: State<Float>
) {
    val color by color
    val rotation by rotation
    val animateFront by animateFront
    val animateBack by animateBack
}


@Composable
private fun updateTransitionData(boxState: CardFace): TransitionData {
    val transition = updateTransition(boxState, label = "")
    val color = transition.animateColor(
        transitionSpec = {
            tween(500)
        },
        label = ""
    ) { state ->
        when (state) {
            CardFace.Front -> Color.Blue
            CardFace.Back -> Color.Red
        }
    }
    val rotation = transition.animateFloat(
        transitionSpec = {
            tween(500)
        },
        label = ""
    ) { state ->
        when (state) {
            CardFace.Front -> 0f
            CardFace.Back -> 180f
        }
    }

    val animateFront = transition.animateFloat(
        transitionSpec = {
            tween(500)
        },
        label = ""
    ) { state ->
        when (state) {
            CardFace.Front -> 1f
            CardFace.Back -> 0f
        }
    }
    val animateBack = transition.animateFloat(
        transitionSpec = {
            tween(500)
        },
        label = ""
    ) { state ->
        when (state) {
            CardFace.Front -> 0f
            CardFace.Back -> 1f
        }
    }

    return remember(transition) { TransitionData(color, rotation, animateFront, animateBack) }
}


@Preview(showBackground = true)

@Composable
fun FlipCardPreview() {
    var cardFace by remember { mutableStateOf(CardFace.Front) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        FlipCard(
            face = cardFace,
            onClick = {
                cardFace = if (cardFace == CardFace.Front) CardFace.Back else CardFace.Front
            },
            modifier = Modifier.size(200.dp, 300.dp),
            front = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Blue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "FRONT SIDE",
                        color = Color.White
                    )
                }
            },
            back = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BACK SIDE",
                        color = Color.White
                    )
                }
            }
        )
    }
}