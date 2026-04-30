package com.scelio.brainest.presentation.scan

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CameraScreen(
    modifier: Modifier = Modifier,
    captureTrigger: Int = 0,
    isProcessing: Boolean = false,
    onImageCaptured: (String) -> Unit = {},
    onCameraReady: (Boolean) -> Unit = {},
    onCloseRequested: () -> Unit = {}
)
