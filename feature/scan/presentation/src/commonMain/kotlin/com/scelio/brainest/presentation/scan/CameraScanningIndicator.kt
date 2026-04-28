package com.scelio.brainest.presentation.scan

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun CameraScanningIndicator(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "scanning_alpha")
    val alpha = transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanning_alpha_value"
    ).value

    Text(
        text = "Scanning...",
        style = MaterialTheme.typography.labelLarge,
        color = Color.White,
        modifier = modifier
            .alpha(alpha)
            .background(
                color = Color.Black.copy(alpha = 0.35f),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 18.dp, vertical = 10.dp)
    )
}
