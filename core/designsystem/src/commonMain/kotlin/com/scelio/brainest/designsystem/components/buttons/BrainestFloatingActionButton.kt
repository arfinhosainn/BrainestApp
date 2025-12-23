package com.scelio.brainest.designsystem.components.buttons

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BrainestFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
        content = content
    )
}

@Composable
@Preview(showBackground = true)
fun BrainestFloatingActionButtonPreview() {
    BrainestTheme {
        BrainestFloatingActionButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun BrainestFloatingActionDarkButtonPreview() {
    BrainestTheme(darkTheme = true) {
        BrainestFloatingActionButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        }
    }
}


