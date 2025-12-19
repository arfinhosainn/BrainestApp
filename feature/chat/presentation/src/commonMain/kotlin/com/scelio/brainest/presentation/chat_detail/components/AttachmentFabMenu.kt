package com.scelio.brainest.presentation.chat_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import brainest.feature.chat.presentation.generated.resources.Res
import brainest.feature.chat.presentation.generated.resources.ic_mic
import org.jetbrains.compose.resources.painterResource

@Composable
fun AttachmentFabMenu(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onDocumentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.size(40.dp)) {
        // Main FAB button
        FloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            containerColor = Color(0xFFEAE6E2),
            contentColor = Color.Black.copy(alpha = 0.75f),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
            shape = CircleShape,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Open attachment menu",
                modifier = Modifier.size(20.dp)
            )
        }

        // Dropdown Menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Gallery option
            DropdownMenuItem(
                text = { Text("Pick from Gallery") },
                onClick = {
                    expanded = false
                    onGalleryClick()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_mic),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black.copy(alpha = 0.75f)
                    )
                }
            )

            // Camera option
            DropdownMenuItem(
                text = { Text("Capture with Camera") },
                onClick = {
                    expanded = false
                    onCameraClick()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_mic),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black.copy(alpha = 0.75f)
                    )
                }
            )

            // Document option
            DropdownMenuItem(
                text = { Text("Upload File") },
                onClick = {
                    expanded = false
                    onDocumentClick()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_mic),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black.copy(alpha = 0.75f)
                    )
                }
            )
        }
    }
}