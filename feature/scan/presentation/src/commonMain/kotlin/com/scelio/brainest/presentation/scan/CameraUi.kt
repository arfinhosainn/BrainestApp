package com.scelio.brainest.presentation.scan

import androidx.compose.ui.unit.dp

internal val CameraControlsReservedHeight = 128.dp
internal const val CameraControlsHeightIncreaseRatio = 0.25f
internal val CameraControlsPanelHeight = CameraControlsReservedHeight * (1f + CameraControlsHeightIncreaseRatio)
internal val CameraControlsOverlapHeight = 17.dp
internal val CameraPreviewBottomPadding = CameraControlsPanelHeight - CameraControlsOverlapHeight
internal val CameraPreviewBottomCornerRadius = 24.dp
internal const val CameraPreviewBottomShrinkRatio = -0.01f
internal const val CameraControlsButtonLiftRatio = 0.05f
