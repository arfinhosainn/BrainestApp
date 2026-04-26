package com.scelio.brainest.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Simple top app bar for Study Sets screen. Keeps title at the start for a clean layout.
 */
@Composable
fun TopAppBarStudySets(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String
) {
    @OptIn(ExperimentalMaterial3Api::class)
    TopAppBar(
        title = {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

            }
        },
        modifier = modifier.fillMaxWidth(),
        // Avoid the newer topAppBarColors overload here because Live Edit can resolve
        // against an older Material3 runtime and crash on signature mismatch.
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}
