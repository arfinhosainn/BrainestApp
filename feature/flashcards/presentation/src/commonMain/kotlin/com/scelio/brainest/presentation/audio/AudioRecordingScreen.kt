package com.scelio.brainest.presentation.audio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.audio.ArcPlayback
import com.scelio.brainest.designsystem.components.audio.WaveForm
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

private val AudioBackground = Color(0xFF19C472).copy(alpha = 0.1f)
private val AudioTextPrimary = Color(0xFF1E2633)
private val AudioTextSecondary = Color.Black
private val AudioWaveTrack = Color(0xFFD7E3F8)
private val AudioWaveFill = Color(0xFFFFFFFF)

@Composable
fun AudioRecordingScreen(
    onBackClick: () -> Unit = {},
    onPowerClick: () -> Unit = {}
) {
    val waveformRatios = remember {
        (1..50).map {
            Random.nextFloat()
        }
    }

    var showPlayback by remember { mutableStateOf(false) }
    val transcriptScrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        showPlayback = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AudioBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 200.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AudioTopAppBar(onBackClick = onBackClick, onPowerClick = onPowerClick)

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(transcriptScrollState)
                                .padding(horizontal = 30.dp, vertical = 30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Today we discussed campaign performance, content calendar, and " +
                                        "upcoming tasks for the next sprint. The main focus will be improving " +
                                        "conversion rate and optimizing landing pages...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 20.sp,
                                    lineHeight = 28.sp
                                ),
                                color = AudioTextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .align(Alignment.TopCenter)
                                .alpha(if (transcriptScrollState.value > 0) 1f else 0f)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White,
                                            Color.White.copy(alpha = 0f)
                                        )
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))

                    WaveForm(
                        amplitudeBarWidth = 6.dp,
                        amplitudeBarSpacing = 5.dp,
                        powerRatios = waveformRatios,
                        trackColor = Color.White,
                        trackFillColor = Color(0xFF19C472),
                        playerProgress = { 0.62f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showPlayback,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 420)
            ) + fadeIn(animationSpec = tween(durationMillis = 320)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ArcPlayback(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3.3333f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudioTopAppBar(
    onBackClick: () -> Unit,
    onPowerClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Audio",
                style = MaterialTheme.typography.titleSmall,
                color = AudioTextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            AudioTopIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBackClick
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Unspecified,
            navigationIconContentColor = Color.Unspecified,
            titleContentColor = Color.Unspecified,
            actionIconContentColor = Color.Unspecified
        )
    )
}

@Composable
private fun AudioTopIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, radius = 20.dp),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp),
            tint = AudioTextPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAudioRecordingScreen() {
    BrainestTheme {
        AudioRecordingScreen()
    }
}
