package com.scelio.brainest.designsystem.components.dialogs

import androidx.compose.runtime.Composable
import com.scelio.brainest.presentation.util.currentDeviceConfiguration

@Composable
fun BrainestAdaptiveDialogSheetLayout(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val configuration = currentDeviceConfiguration()
    if(configuration.isMobile) {
        BrainestBottomSheet(
            onDismiss = onDismiss,
            content = content
        )
    } else {
        BrainestDialogContent(
            onDismiss = onDismiss,
            content = content
        )
    }
}