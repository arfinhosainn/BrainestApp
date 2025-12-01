package com.scelio.brainest.designsystem.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.layouts.BrainestAdaptiveResultLayout


@Composable
@PreviewLightDark
@PreviewScreenSizes
fun BrainestAdaptiveResultLayoutPreview() {
    BrainestTheme {
        BrainestAdaptiveResultLayout(
            modifier = Modifier
                .fillMaxSize(),
            content = {
                Text(
                    text = "Registration successful!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}