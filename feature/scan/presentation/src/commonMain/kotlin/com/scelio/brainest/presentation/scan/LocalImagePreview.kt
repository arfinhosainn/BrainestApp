package com.scelio.brainest.presentation.scan

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun LocalImagePreview(
    imagePath: String,
    modifier: Modifier = Modifier
)
