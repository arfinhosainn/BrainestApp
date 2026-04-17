package com.scelio.brainest.designsystem.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import brainest.core.designsystem.generated.resources.Res
import brainest.core.designsystem.generated.resources.cancel
import brainest.core.designsystem.generated.resources.delete
import brainest.core.designsystem.generated.resources.delete_profile_picture_description
import brainest.core.designsystem.generated.resources.delete_profile_picture_title
import brainest.core.designsystem.generated.resources.dismiss_dialog
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import com.scelio.brainest.designsystem.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DestructiveConfirmationDialog(
    title: String,
    description: String,
    confirmButtonText: String,
    cancelButtonText: String,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                )
                .widthIn(max = 480.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.extended.textPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.extended.textSecondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrainestButton(
                        text = cancelButtonText,
                        onClick = onCancelClick,
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.extended.disabledOutline)
                    )
                    BrainestButton(
                        text = confirmButtonText,
                        onClick = onConfirmClick,
                        backgroundColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                }
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.dismiss_dialog),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }
        }
    }
}

@Composable
@Preview
fun DestructiveConfirmationDialogPreview() {
    BrainestTheme(darkTheme = true) {
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.delete_profile_picture_title),
            description = stringResource(Res.string.delete_profile_picture_description),
            confirmButtonText = stringResource(Res.string.delete),
            cancelButtonText = stringResource(Res.string.cancel),
            onConfirmClick = {},
            onCancelClick = {},
            onDismiss = {}
        )
    }
}
