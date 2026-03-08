package com.scelio.brainest.designsystem.components.brand

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import brainest.core.designsystem.generated.resources.Res
import brainest.core.designsystem.generated.resources.brainest_logo
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BrainestBrandLogo(
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = vectorResource(Res.drawable.brainest_logo),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = modifier.size(150.dp)
    )
}