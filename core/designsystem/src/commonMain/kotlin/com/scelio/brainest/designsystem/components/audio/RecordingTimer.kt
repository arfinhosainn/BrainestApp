package com.scelio.brainest.designsystem.components.audio

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StopwatchTimer(
    elapsedMillis: Long,
    modifier: Modifier = Modifier
) {
    val totalSeconds = elapsedMillis / 1000L
    val hours = (totalSeconds / 3600).toString().padStart(2, '0')
    val minutes = ((totalSeconds % 3600) / 60).toString().padStart(2, '0')
    val seconds = (totalSeconds % 60).toString().padStart(2, '0')

    val timeString = "$hours:$minutes:$seconds"

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
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
    BrainestTheme { StopwatchTimer(elapsedMillis = 65_000L) }
}
