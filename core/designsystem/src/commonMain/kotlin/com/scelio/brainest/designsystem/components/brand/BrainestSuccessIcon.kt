package com.scelio.brainest.designsystem.components.brand

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import brainest.core.designsystem.generated.resources.Res
import brainest.core.designsystem.generated.resources.success_checkmark
import com.scelio.brainest.designsystem.extended
import org.jetbrains.compose.resources.vectorResource


@Composable
fun BrainestSuccessIcon(
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = vectorResource(Res.drawable.success_checkmark),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.extended.success,
        modifier = modifier
    )
}