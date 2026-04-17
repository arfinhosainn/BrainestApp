package com.scelio.brainest.designsystem.components.audio



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import brainest.core.designsystem.generated.resources.Res
import brainest.core.designsystem.generated.resources.paused
import brainest.core.designsystem.generated.resources.play
import brainest.core.designsystem.generated.resources.stopped
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun EchoPlaybackButton(
    playbackState: PlaybackState,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    colors: IconButtonColors,
    iconSize: androidx.compose.ui.unit.Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = when (playbackState) {
            PlaybackState.PLAYING -> onPauseClick
            PlaybackState.PAUSED,
            PlaybackState.STOPPED -> onPlayClick
        },
        colors = colors,
        modifier = modifier
            .defaultShadow()
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = when (playbackState) {
                PlaybackState.PLAYING -> Icons.Filled.Pause
                PlaybackState.PAUSED,
                PlaybackState.STOPPED -> Icons.Filled.PlayArrow
            },
            contentDescription = when (playbackState) {
                PlaybackState.PLAYING -> stringResource(Res.string.play)
                PlaybackState.PAUSED -> stringResource(Res.string.paused)
                PlaybackState.STOPPED -> stringResource(Res.string.stopped)
            }
        )
    }
}

@Preview
@Composable
private fun EchoPlaybackButtonPreview() {
    BrainestTheme {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            EchoPlaybackButton(
                playbackState = PlaybackState.PAUSED,
                onPauseClick = {},
                onPlayClick = {},
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF19C472),
                    contentColor = Color.White
                )
            )
            EchoPlaybackButton(
                playbackState = PlaybackState.PLAYING,
                onPauseClick = {},
                onPlayClick = {},
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF19C472),
                    contentColor = Color.White
                )
            )

            EchoPlaybackButton(
                playbackState = PlaybackState.STOPPED,
                onPauseClick = {},
                onPlayClick = {},
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF19C472),
                    contentColor = Color.White
                )
            )
        }

    }
}


fun Modifier.defaultShadow(shape: Shape = CircleShape): Modifier {
    return this.shadow(
        elevation = 4.dp,
        shape = shape,
        ambientColor = DefaultShadowColor.copy(alpha = 0.3f),
        spotColor = DefaultShadowColor.copy(alpha = 0.3f)
    )
}
