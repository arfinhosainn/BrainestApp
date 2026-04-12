package com.scelio.brainest.presentation.audio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.audio.ArcPlayback
import com.scelio.brainest.designsystem.components.audio.PlaybackState
import com.scelio.brainest.designsystem.components.audio.WaveForm
import com.scelio.brainest.domain.auth.AuthInfo
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.AudioChunkData
import com.scelio.brainest.flashcards.domain.AudioTranscriptionError
import com.scelio.brainest.flashcards.domain.AudioTranscriptionResult
import com.scelio.brainest.flashcards.domain.AudioTranscriptionService
import com.scelio.brainest.flashcards.domain.Deck
import com.scelio.brainest.flashcards.domain.Flashcard
import com.scelio.brainest.flashcards.domain.FlashcardInput
import com.scelio.brainest.flashcards.domain.FlashcardsGenerationError
import com.scelio.brainest.flashcards.domain.FlashcardsGenerationService
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.flashcards.domain.SessionRecordInput
import com.scelio.brainest.flashcards.domain.SessionSummary
import com.scelio.brainest.flashcards.domain.StudySession
import com.scelio.brainest.presentation.permission.Permission
import com.scelio.brainest.presentation.permission.PermissionState
import com.scelio.brainest.presentation.permission.rememberPermissionController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

private val AudioBackground = Color(0xFF19C472).copy(alpha = 0.1f)
private val AudioTextPrimary = Color(0xFF1E2633)
private val AudioTextSecondary = Color.Black
private val AudioWaveTrack = Color(0xFFD7E3F8)

@Composable
fun AudioRecordingScreen(
    onBackClick: () -> Unit = {},
    onStudySetReady: (String) -> Unit = {},
    viewModel: AudioRecordingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val permissionController = rememberPermissionController()
    var permissionState by remember { mutableStateOf(PermissionState.NOT_DETERMINED) }

    var showPlayback by remember { mutableStateOf(false) }
    val transcriptScrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        showPlayback = true
        permissionState = permissionController.requestPermission(Permission.MICROPHONE)
        if (permissionState == PermissionState.GRANTED) {
            viewModel.startRecording()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is AudioRecordingEvent.StudySetReady -> onStudySetReady(event.deckId)
            }
        }
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
            AudioTopAppBar(
                onBackClick = {
                    viewModel.stopRecording()
                    onBackClick()
                }
            )

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
                            if (state.transcript.isNotBlank()) {
                                Text(
                                    text = state.transcript,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 20.sp,
                                        lineHeight = 28.sp
                                    ),
                                    color = AudioTextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                                )
                            } else {
                                Text(
                                    text = "Listening...",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 20.sp,
                                        lineHeight = 28.sp
                                    ),
                                    color = AudioTextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                                )
                            }
                        }

                        if (permissionState != PermissionState.GRANTED) {
                            Text(
                                text = when (permissionState) {
                                    PermissionState.PERMANENTLY_DENIED ->
                                        "Microphone permission is blocked. Enable it in Settings."

                                    PermissionState.DENIED ->
                                        "Microphone permission is needed to record audio."

                                    PermissionState.NOT_DETERMINED ->
                                        "Requesting microphone permission..."

                                    PermissionState.GRANTED -> ""
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = AudioTextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 24.dp)
                            )
                        }

                        val generationError = state.generationError
                        if (generationError != null) {
                            Text(
                                text = generationError,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFF84E40),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
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

                    Spacer(modifier = Modifier.height(80.dp))

                    WaveForm(
                        amplitudeBarWidth = 6.dp,
                        amplitudeBarSpacing = 5.dp,
                        powerRatios = state.amplitudes,
                        trackColor = Color.White,
                        trackFillColor = Color(0xFF19C472),
                        playerProgress = { 0f },
                        useProgressFill = false,
                        silenceThreshold = 0.06f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showPlayback,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 520,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 260,
                    easing = FastOutSlowInEasing
                )
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ArcPlayback(
                playbackState = when (state.status) {
                    RecordingStatus.RECORDING -> PlaybackState.PLAYING
                    RecordingStatus.PAUSED,
                    RecordingStatus.STOPPED -> PlaybackState.PAUSED
                },
                elapsedMillis = state.elapsedMillis,
                onTogglePlayback = {
                    if (permissionState == PermissionState.GRANTED && !state.isGenerating) {
                        viewModel.togglePauseResume()
                    }
                },
                onConfirm = {
                    if (permissionState == PermissionState.GRANTED && !state.isGenerating) {
                        viewModel.finishRecordingAndGenerate()
                    }
                },
                onCancel = {
                    viewModel.stopRecording()
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3.3333f)
            )
        }

        if (state.isGenerating) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "Preparing study set...",
                        style = MaterialTheme.typography.titleMedium,
                        color = AudioTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = Color(0xFF19C472),
                        trackColor = AudioWaveTrack
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "This can take a few seconds.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AudioTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
