package com.scelio.brainest.designsystem.components.audio

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StopwatchTimer() {
    var totalSeconds by remember { mutableLongStateOf(0L) }
    val isRunning by remember { mutableStateOf(true) }

    // Tick every second
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            totalSeconds++
        }
    }

    // Replace String.format with Kotlin's padStart
    val hours = (totalSeconds / 3600).toString().padStart(2, '0')
    val minutes = ((totalSeconds % 3600) / 60).toString().padStart(2, '0')
    val seconds = (totalSeconds % 60).toString().padStart(2, '0')

    val timeString = "$hours:$minutes:$seconds"

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = timeString,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A),
            letterSpacing = 2.sp
        )
    }
}

@Preview
@Composable
fun PreviewRecorTimer() {
    BrainestTheme { StopwatchTimer() }
}
